package com.uzem.book_cycle.rental.service;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.rental.dto.*;
import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.rental.entity.RentalHistory;
import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.reservation.entity.Reservation;
import com.uzem.book_cycle.rental.repository.RentalHistoryRepository;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.entity.OrderItem;
import com.uzem.book_cycle.external.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.external.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.external.payment.service.PaymentService;
import com.uzem.book_cycle.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.rental.type.RentalErrorCode.*;
import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.order.type.ItemType.RENTAL;
import static com.uzem.book_cycle.order.type.OrderErrorCode.ORDER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final MemberRepository memberRepository;
    private final AdminRentalRepository rentalRepository;
    private final OrderRepository orderRepository;

    private static final int PAYMENT_DEADLINE_DAYS = 1;

    // 대여 이력 생성
    @Transactional
    public void createRentalHistory(Long rentalBookId, Long memberId,
                                    Long orderId, LocalDate now) {
        // 1. 락 걸고 도서 조회
        RentalBook rentalBook = rentalRepository.findByIdWithLock(rentalBookId)
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        if(rentalBook.isRented()){
            throw new RentalException(ALREADY_RENTED);
        }

        // 2. 상태 변경
        rentalBook.rented();

        // 3. 연관 객체 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        // 4. 대여 이력 생성
        RentalHistory rentalHistory = RentalHistory.from(rentalBook, member, order, now);
        rentalHistoryRepository.save(rentalHistory);
    }

    // 반납하기
    @Transactional
    public GroupReturnResponseDTO returnRental(Long orderId, Long memberId,
                                               PaymentRequestDTO payment) {
        Member member = findByMemberId(memberId);
        List<RentalHistory> rentalHistories = rentalHistoryRepository.findAllByOrderId(orderId);

        // 반납 처리 전 도서 상태 변경
        for(RentalHistory rentalHistory : rentalHistories){
            RentalBook rentalBook = rentalHistory.getRentalBook();
            boolean hasReservation =
                    rentalBook.getReservations().stream().anyMatch(Reservation::isActive);
            // 예약자 있음
            if(hasReservation) {
                rentalBook.updatePendingPayment(); // 결제대기 상태로 변경
                reservationService.updateReservationPaymentDeadline(rentalBook); // 예약순번 1번 결제기한 부여
            } else{
                rentalBook.updateAvailable(); // 대여가능 변경
            }
        }

        // 반납도서상태 검증
        RentalStatus rentalStatus = validGroupRentalStatus(rentalHistories);
        if(rentalStatus == RENTED){
            returnAllRentals(member, rentalHistories); // 반납처리
            reservationService.notifyNextReservation(rentalHistories);
            return GroupReturnResponseDTO.from(rentalHistories, null);
        } else{ // 연체 상태
            long totalOverdueFee = rentalHistories.stream()
                    .mapToLong(RentalHistory::getOverdueFee)
                    .sum(); // 연체료 합산
            payment.updateTotalOverdueAmount(totalOverdueFee);
            PaymentResponseDTO paymentResponseDTO = paymentService.processOverduePayment(payment);// 결제 승인
            returnAllRentals(member, rentalHistories); // 반납 처리
            reservationService.notifyNextReservation(rentalHistories); // 알림 전송

            // 결제 후 rentalHistoryResponseList 생성
            List<RentalHistoryResponseDTO> rentalHistoryResponseList = rentalHistories.stream()
                    .map(history -> RentalHistoryResponseDTO
                            .from(history, paymentResponseDTO)) // 결제 응답 넣어줌
                    .collect(Collectors.toList());

            return GroupReturnResponseDTO.fromHistoryResponse(rentalHistoryResponseList, paymentResponseDTO);
        }

    }

    private static RentalStatus validGroupRentalStatus(List<RentalHistory> rentalHistories) {
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
    public RentalResponseDTO cancelPendingPayment(RentalBook rentalBook, Long memberId) {
        Reservation reservation = reservationService.getReservation(rentalBook, memberId);
        if(reservation.getRentalBook().getRentalStatus() == PENDING_PAYMENT) {
            reservationService.cancelPendingReservation(reservation);
        }
        return RentalResponseDTO.from(rentalBook);
    }



    // 대여 조회
    @Override
    public List<RentalHistoryResponseDTO> getMyRentals(Long memberId) {
        List<RentalHistory> rentalHistories = rentalHistoryRepository.
                findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(RENTED, memberId);
        // 대여도서 상태 검증
        for(RentalHistory rentalHistory : rentalHistories){
            if(rentalHistory.getRentalStatus() != RENTED) {
                throw new RentalException(INVALID_RENTAL_STATUS);
            }
        }
        return rentalHistories.stream()
                .map(RentalHistoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 연체 조회
    @Override
    public List<OverdueListResponseDTO> getMyOverdue(Long memberId) {
        // 연체도서 조회
        List<RentalHistory> rentalHistories = rentalHistoryRepository.
                findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(RentalStatus.OVERDUE, memberId);
        // 대여도서 상태 검증
        for(RentalHistory rentalHistory : rentalHistories){
            if(rentalHistory.getRentalStatus() != RentalStatus.OVERDUE) {
                throw new RentalException(INVALID_RENTAL_STATUS);
            }
        }
        // 연체 이력 리스트 → OverdueDetailDTO 변환
        List<OverdueDetailDTO> detail = rentalHistories.stream()
                .map(OverdueDetailDTO::from)
                .toList();

        // 주문단위로 묶음
        Map<Long, List<OverdueDetailDTO>> list = detail.stream()
                .collect(Collectors.groupingBy(OverdueDetailDTO::getOrderId));

        return list.values().stream()
                .map(OverdueListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 대여이력 조회
    @Override
    public List<RentalHistoryListResponseDTO> getMyRentalHistories(Long memberId) {
        // 대여이력 조회
        List<RentalHistory> rentalHistories = rentalHistoryRepository.
                findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(RETURNED, memberId);
        // 대여도서 상태 검증
        for(RentalHistory rentalHistory : rentalHistories){
            if(rentalHistory.getRentalStatus() != RETURNED) {
               throw new RentalException(INVALID_RENTAL_STATUS);
            }
        }
        //결제 정보
        PaymentResponseDTO payment = paymentService.getOverduePayment(rentalHistories.get(0).getOrder());
        List<RentalHistoryResponseDTO> detail = rentalHistories.stream()
                .map(history -> RentalHistoryResponseDTO.from(history, payment)) // 결제정보 넣어줌
                .toList();

        // 주문단위로 묶음
        Map<Long, List<RentalHistoryResponseDTO>> list = detail.stream()
                .collect(Collectors.groupingBy(RentalHistoryResponseDTO::getOrderId));

        // RentalHistoryListResponseDTO 변환
        return list.values().stream()
                .map(RentalHistoryListResponseDTO::from)
                .collect(Collectors.toList());
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }


    public void restoreRentalBookStatus(RentalBook rentalBook) {
        boolean hasReservation = rentalBook.getReservations().stream()
                .anyMatch(Reservation::isActive);
        if(hasReservation) { // 예약자 있음
            rentalBook.updatePendingPayment(); // 결제대기 상태로 변경

            // 예약순번 1번 결제기한 부여
            rentalBook.getReservations().stream()
                    .filter(Reservation::isActive)
                    .min(Comparator.comparingInt(Reservation::getReservationOrder))
                    .ifPresent(reservation ->
                            reservation.updatePaymentDeadline(LocalDate.now()
                                    .plusDays(PAYMENT_DEADLINE_DAYS))
                    );
        } else{
            rentalBook.updateAvailable(); // 대여가능 변경
        }
    }

    public void restoreRentalHistory(Order order, RentalBook rentalBook) {
        for(OrderItem item : order.getOrderItems()){
            if(item.getItemType() == RENTAL){
                RentalHistory rentalHistory = rentalHistoryRepository
                        .findByOrderAndRentalBook(order, item.getRentalBook())
                        .orElseThrow(() -> new RentalException(RENTAL_HISTORY_NOT_FOUND));

                rentalHistory.cancel();
            }
        }
    }
}
