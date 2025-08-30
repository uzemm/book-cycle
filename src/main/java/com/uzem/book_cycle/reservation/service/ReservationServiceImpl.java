package com.uzem.book_cycle.reservation.service;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.reservation.dto.ReservationResponseDTO;
import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.rental.entity.RentalHistory;
import com.uzem.book_cycle.event.ReservationFirstEvent;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.exception.ReservationException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.notification.type.NotificationType;
import com.uzem.book_cycle.reservation.entity.Reservation;
import com.uzem.book_cycle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.rental.type.RentalErrorCode.*;
import static com.uzem.book_cycle.rental.type.RentalErrorCode.PENDING_PAYMENT_RESERVATION_CANNOT_BE_CANCELED;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.reservation.type.ReservationErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;
    private final MemberRepository memberRepository;
    private final AdminRentalRepository adminRentalRepository;

    // 예약하기
    @Transactional
    public ReservationResponseDTO createReservation(Long rentalBookId, Long memberId) {

        RentalBook rentalBook = adminRentalRepository.findById(rentalBookId)
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));
        Member member = findByMemberId(memberId);

        if(!rentalBook.getRentalStatus().canReserve()) { // 대여중 여부
            throw new ReservationException(RESERVATION_ALLOWED_ONLY_WHEN_RENTED);
        }
        if(reservationRepository.existsByRentalBookAndMemberAndIsActiveTrue(rentalBook, member)){ // 예약자 조회
            throw new ReservationException(RESERVATION_ALREADY_EXISTS);
        }
        // 예약 횟수
        long reservationCount = rentalBook.getReservations().stream()
                .filter(Reservation::isActive)
                .count();
        if(reservationCount >= 2){ // 예약 꽉 찼을 때
            throw new ReservationException(RESERVATION_FULL);
        }
        Reservation reservation = Reservation.create(rentalBook, member); // 연관관계 설정

        // 예약 순서 설정
        reservation.updateReservationOrder((int) reservationCount + 1);
        rentalBook.addReservation(reservation); // 예약 추가
        reservationRepository.save(reservation);

        return ReservationResponseDTO.from(reservation);
    }

    // 예약 + 결제대기 조회
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getMyReservations(Long memberId) {
        List<Reservation> reservations = reservationRepository.findAllByMemberIdAndIsActiveTrue(memberId);

        return reservations.stream()
                .map(ReservationResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 예약 취소
    @Transactional
    public void cancelMyReservation(RentalBook rentalBook, Long memberId) {
        Reservation reservation = getReservation(rentalBook, memberId);

        RentalStatus rentalStatus = reservation.getRentalBook().getRentalStatus();
        if(rentalStatus == RENTED || rentalStatus == RentalStatus.OVERDUE){ // 대여 or 연체
            reservation.cancelReservation();// isActive = false, 예약순번 초기화
            reorderReservations(rentalBook); // 예약순번 재정렬
            String message = NotificationType.RENTAL_OVERDUE.format(
                    rentalBook.getTitle(), 0);
            //알림전송
            eventPublisher.publishEvent(
                    new ReservationFirstEvent(rentalBook, message));
        } else{
            throw new RentalException(PENDING_PAYMENT_RESERVATION_CANNOT_BE_CANCELED);
        }
    }

    @Transactional(readOnly = true)
    public Reservation getReservation(RentalBook rentalBook, Long memberId) {
        return reservationRepository.findByRentalBookAndMemberIdAndIsActiveTrue(
                rentalBook, memberId).orElseThrow(
                () -> new ReservationException(RESERVATION_NOT_FOUND));
    }

    // 결제 성공 후 호출
    @Transactional
    public void afterPaymentSuccess(Long rentalId, Member member){
        handleReservation(rentalId, member, true);
    }

    // 결제 취소/기한 만료 후 호출 (배치 등)
    @Transactional
    public void afterCancelOrExpire(Long rentalId, Member member){
        handleReservation(rentalId, null, false);
    }

    @Transactional
    public void handleReservation(Long rentalId, Member member, boolean isPayment){
        RLock lock = redissonClient.getLock("reservation_" + rentalId);

        try{
            // 락 시도: 최대 5초 대기, 10초 점유
            if(lock.tryLock(5, 10, TimeUnit.SECONDS)){
                // 1. 순번 조회
                List<Reservation> reservations = reservationRepository
                        .findAllByRentalBookIdOrderByReservationOrderAsc(rentalId);

                if(reservations.isEmpty()){
                    throw new ReservationException(RESERVATION_NOT_FOUND);
                }

                // 2. 1순위 가져오기
                Reservation first = reservations.get(0);

                if(isPayment){
                    // 결제 성공 → 1순위 활성화 해제 + 도서 상태 RENTED 유지
                    if(first.getMember().equals(member) && first.isActive()){
                        first.cancelReservation(); // isActive=false
                    } else{
                        throw new ReservationException(INVALID_RESERVATION_ORDER);
                    }
                } else{
                    // 결제 취소 → 1순위 비활성화
                    first.cancelReservation();
                }

                // 3. 순번 재정렬
                reorderReservations(reservations);

                // 4. 저장
                reservationRepository.saveAll(reservations);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            // 5. 락 해제
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    private static void reorderReservations(List<Reservation> reservations) {
        int seq = 1;
        for(Reservation reservation : reservations){
            if(reservation.isActive()){
                reservation.updateReservationOrder(seq++);
            }
        }
    }

    private static void reorderReservations(RentalBook rentalBook) {
        reorderReservations(rentalBook.getReservations());
    }

    // 결제대기 취소
    public void cancelPendingReservation(Reservation reservation) {
        if(reservation.isPendingPayment()) {
            RentalBook rentalBook = reservation.getRentalBook();
            rentalBook.updateAvailable(); // 대여도서 상태 초기화
            reservation.cancelReservation();// isActive = false

            // 예약자 있음
            boolean hasReservation = rentalBook.getReservations().stream()
                    .anyMatch(Reservation::isActive);
            if(hasReservation) {
                activateNextReservationAndNotify(rentalBook); // 예약자 순번 재정렬 및 알림 전송
            }
        }
    }

    private void activateNextReservationAndNotify(RentalBook rentalBook) {
        reorderReservations(rentalBook); // 예약순번 재정렬
        rentalBook.updatePendingPayment(); // 결제대기 상태로 변경
        updateReservationPaymentDeadline(rentalBook);
        //알림 전송
        String message = NotificationType.RENTAL_OVERDUE.format(
                rentalBook.getTitle(), 0);
        eventPublisher.publishEvent(
                new ReservationFirstEvent(rentalBook, message));
    }

    public void updateReservationPaymentDeadline(RentalBook rentalBook) {
        LocalDate deadline = LocalDate.now().plusDays(1);
        rentalBook.getReservations().stream()
                .filter(r -> r.isActive() && r.getReservationOrder() == 1)
                .findFirst()
                .ifPresent(r -> r.updatePaymentDeadline(deadline));
    }

    // 결제대기 기간 만료 배치
    @Transactional
    public void updateCancelPendingPayment(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            try {
                cancelPendingReservation(reservation); // 결제대기 취소
            } catch (Exception e) {
                log.warn("자동 예약 취소 실패 - 예약ID: {}, 이유: {}",
                        reservation.getId(), e.getMessage());
            }
        }
    }

    public void notifyNextReservation(List<RentalHistory> rentalHistories) {
        for(RentalHistory rentalHistory : rentalHistories){
            RentalBook rentalBook = rentalHistory.getRentalBook();
            boolean hasReservation =
                    rentalBook.getReservations().stream().anyMatch(Reservation::isActive);
            if(hasReservation){
                String message = NotificationType.RENTAL_OVERDUE.format(
                        rentalBook.getTitle(), 0);
                //알림전송
                eventPublisher.publishEvent(
                        new ReservationFirstEvent(rentalBook, message));
            }
        }
    }

    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }
}
