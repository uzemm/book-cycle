package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.dto.GroupReturnResponseDTO;
import com.uzem.book_cycle.book.dto.RentalHistoryResponseDTO;
import com.uzem.book_cycle.book.dto.RentalResponseDTO;
import com.uzem.book_cycle.book.dto.ReservationResponseDTO;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.*;
import static com.uzem.book_cycle.admin.type.RentalStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final OverduePolicy overduePolicy;
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    public void createRentalHistory(RentalBook rentalBook, Member member,
                                    Order order, LocalDate now) {
        RentalHistory rentalHistory = RentalHistory.from(rentalBook, member, order, now);
        rentalHistoryRepository.save(rentalHistory);
    }

    // 연체 배치 처리
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateStatusOverdue() {
        LocalDate now = LocalDate.now();
        List<RentalHistory> rentalHistories = rentalHistoryRepository.findAllByRentalStatus(RENTED);

        for(RentalHistory rentalHistory : rentalHistories) {
            try{
                LocalDate returnDate = rentalHistory.getReturnDate();
                if(returnDate.isBefore(now)) {
                    rentalHistory.statusOverdue(); // rented -> overdue
                    long overdueFee = calculateOverdueFee(rentalHistory, now); // 연체료 계산
                    rentalHistory.setOverdueFee(overdueFee); // 연체료 저장
                }
            } catch (Exception e){
                log.warn("자동 연체 처리 실패 - 대여이력ID: {}, 이유: {}",
                        rentalHistory.getId(), e.getMessage());
            }

        }
    }

    // 연체료 계산
    @Override
    public long calculateOverdueFee(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        long overdueDays = ChronoUnit.DAYS.between(returnDate, now);

        return  overduePolicy.calculateOverdue(rentalHistory, overdueDays);
    }

    // 예약하기
    @Override
    public ReservationResponseDTO createReservation(RentalBook rentalBook, Member member) {
        if(!rentalBook.isRented()) {
            throw new RentalException(BOOK_NOT_RENTED);
        }
        if(reservationRepository.existsByRentalBookAndMember(rentalBook, member)){ // 예약자 조회
            throw new RentalException(ALREADY_RESERVED_THIS_BOOK);
        }
        if(reservationRepository.existsByRentalBook(rentalBook)){
            throw new RentalException(ALREADY_RESERVED_BY_OTHER);
        }
        Reservation reservation = Reservation.create(rentalBook, member); // 연관관계 설정
        reservationRepository.save(reservation);

        return ReservationResponseDTO.from(reservation);
    }

    // 예약 + 결제대기 조회
    @Override
    public List<ReservationResponseDTO> getMyReservations(Member member) {
        List<Reservation> reservations = reservationRepository.findAllByMember(member);

        return reservations.stream()
                .map(ReservationResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 예약 취소
    @Override
    public void cancelMyReservation(RentalBook rentalBook, Member member) {
        Reservation reservation = reservationRepository.findByRentalBookAndMember(
                rentalBook, member).orElseThrow(
                () -> new RentalException(RESERVATION_NOT_FOUND));
        RentalStatus rentalStatus = reservation.getRentalBook().getRentalStatus();
        if(rentalStatus == RENTED || rentalStatus == OVERDUE){
            reservation.deleteRentalBook(); // 연관관계 끊기
            reservationRepository.delete(reservation);
        } else{
            throw new RentalException(CANNOT_CANCEL_PENDING_PAYMENT);
        }
    }

    // 반납하기
    @Transactional
    public GroupReturnResponseDTO returnRental(Long orderId, Member member,
                                               PaymentRequestDTO payment) {
        List<RentalHistory> rentalHistories = rentalHistoryRepository.findAllByOrderId(orderId);

        // 반납도서상태 검증
        RentalStatus rentalStatus = validGroupRentalStatus(rentalHistories);

        if(rentalStatus== RENTED){
            returnAllRentals(member, rentalHistories);
        } else{ // 연체 상태
            long totalOverdueFee = rentalHistories.stream()
                    .mapToLong(RentalHistory::getOverdueFee)
                    .sum();
            payment.updateTotalOverdueAmount(totalOverdueFee); // 연체료 합산
            paymentService.processPayment(payment); // 결제 승인
            returnAllRentals(member, rentalHistories); //
        }

        for(RentalHistory rentalHistory : rentalHistories){
            RentalBook rentalBook = rentalHistory.getRentalBook();
            if(rentalBook.getReservation()!= null) { // 예약자 있음
                rentalBook.updatePendingPayment(); // pending_payment(결제대기)
                rentalBook.getReservation().updatePaymentDeadline(
                        LocalDate.now().plusDays(1)); // 결제대기기한 설정
            } else{
                rentalBook.updateAvailable(); // 대여가능 변경
            }
        }

        return GroupReturnResponseDTO.from(rentalHistories, payment);
    }

    private static RentalStatus validGroupRentalStatus(List<RentalHistory> rentalHistories) {
        System.out.println("rentalHistories = " + rentalHistories);
        RentalStatus rentalStatus = rentalHistories.get(0).getRentalStatus();
        boolean status = rentalHistories.stream() // rented or overdue
                .allMatch(history -> history.getRentalStatus() == rentalStatus);
        if(!status){ // 묶음대여도서 상태 불일치
            throw new RentalException(RENTAL_HISTORY_STATUS_MISMATCH);
        }
        return rentalStatus;
    }

    private void returnAllRentals(Member member, List<RentalHistory> rentalHistories) {
        for(RentalHistory rentalHistory : rentalHistories){
            updateReturned(rentalHistory, member);
        }
    }

    // 반납 상태 변경
    private void updateReturned(RentalHistory rentalHistory, Member member) {
        if(rentalHistory.getRentalStatus() == RENTED) { // rented 상태
            rentalHistory.updateReturned(LocalDate.now()); // RETURNED
        } else{ // overdue(연체) 상태
            rentalHistory.updateOverdueReturned(); // RETURNED
        }
        member.returnRentalCnt(); // 0
    }

    // 결제대기 취소 처리
    @Override
    @Transactional
    public RentalResponseDTO cancelPendingPayment(RentalBook rentalBook, Member member) {
        Reservation reservation = reservationRepository.findByRentalBookAndMember(
                rentalBook, member).orElseThrow(
                () -> new RentalException(RESERVATION_NOT_FOUND));
        if(reservation.getRentalBook().getRentalStatus() == PENDING_PAYMENT) {
            cancelPendingReservation(reservation);
        }
        return RentalResponseDTO.from(rentalBook);
    }

    // 결제대기 취소
    private void cancelPendingReservation(Reservation reservation) {
        if(reservation.isPendingPayment()) {
            RentalBook rentalBook = reservation.getRentalBook();
            rentalBook.updateAvailable(); // 대여도서 상태 초기화
            reservation.deleteRentalBook(); // 연관관계 끊기
            reservationRepository.delete(reservation); // 예약 삭제
        }
    }

    // 결제대기 기간 만료 배치
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateCancelPendingPayment() {
        LocalDate now = LocalDate.now();
        List<Reservation> reservations = reservationRepository
                .findAllByRentalBook_RentalStatus(PENDING_PAYMENT);

        for (Reservation reservation : reservations) {
            try {
                LocalDate paymentDeadline = reservation.getPaymentDeadline();
                if (paymentDeadline.isBefore(now)) {
                    cancelPendingReservation(reservation); // 결제대기 취소
                }
            } catch (Exception e) {
                log.warn("자동 예약 취소 실패 - 예약ID: {}, 이유: {}",
                        reservation.getId(), e.getMessage());
            }
        }
    }

    // 대여 조회
    @Override
    public List<RentalHistoryResponseDTO> getMyRentals(Member member) {
        List<RentalHistory> rentalHistories = rentalHistoryRepository.
                findAllByRentalStatusAndMember(RENTED, member);

        return rentalHistories.stream()
                .map(RentalHistoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 연체 조회
    @Override
    public List<RentalHistoryResponseDTO> getMyOverdue(Member member) {
        List<RentalHistory> rentalHistories = rentalHistoryRepository.
                findAllByRentalStatusAndMember(OVERDUE, member);

        return rentalHistories.stream()
                .map(RentalHistoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 대여이력 조회
    @Override
    public List<RentalHistoryResponseDTO> getMyRentalHistories(Member member) {
        List<RentalHistory> rentalHistories = rentalHistoryRepository.
                findAllByRentalStatusAndMember(RETURNED, member);

        return rentalHistories.stream()
                .map(RentalHistoryResponseDTO::from)
                .collect(Collectors.toList());
    }
}
