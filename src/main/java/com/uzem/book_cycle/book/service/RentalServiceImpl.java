package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.dto.ReservationResponseDTO;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.member.entity.Member;
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

    public void createRentalHistory(RentalBook rentalBook, Member member, LocalDate now) {
        RentalHistory rentalHistory = RentalHistory.from(rentalBook, member, now);
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

    // 예약 조회
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



}
