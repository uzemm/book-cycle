package com.uzem.book_cycle.notification.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.dto.NotifyDTO;
import com.uzem.book_cycle.notification.entity.Notification;
import com.uzem.book_cycle.notification.repository.NotificationRepository;
import com.uzem.book_cycle.notification.type.RentalOverdueNotificationPolicy;
import com.uzem.book_cycle.order.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.notification.type.NotificationType.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ReservationRepository reservationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RentalHistoryRepository rentalHistoryRepository;


    // 예약자 순번 알림 전송
    @Override
    public void notifyNextReservationIfExists(RentalBook rentalBook) {
        // 알림 저장
        reservationRepository
                .findFirstByRentalBookAndRentalBook_RentalStatusAndIsActiveTrueOrderByReservationOrderAsc(
                        rentalBook, PENDING_PAYMENT)
                .ifPresent(next -> {
                    // 알림 중복 여부
                    if (!reservationRepository.existsByMemberAndRentalBookAndPaymentDeadline(
                            next.getMember(),
                            next.getRentalBook(),
                            next.getPaymentDeadline())) {
                        notificationRepository.save(Notification.builder()
                                .member(next.getMember())
                                .rentalBook(next.getRentalBook())
                                .type(RESERVATION_FIRST)
                                .message(RESERVATION_FIRST.getDefaultMessage())
                                .build()
                        );
                    }

                    // 웹소켓 알림 전송
                    String message = String.format("[%s] %s",
                            next.getRentalBook().getTitle(),
                            RESERVATION_FIRST.getDefaultMessage());
                    NotifyDTO notifyDTO = NotifyDTO.of(RESERVATION_FIRST, message);
                    try {
                        messagingTemplate.convertAndSend(
                                "/sub/member/" + next.getMember().getId(), notifyDTO);
                    } catch (Exception e) {
                        log.warn("예약 알림 전송 실패: memberId={}, 이유={}",
                                next.getMember().getId(), e.getMessage());
                    }
                });
    }

    @Override
    public void notifyRentalOverdue(RentalHistory rentalHistory) {
        // 알림 저장
        boolean overdue = rentalHistoryRepository.existsByRentalStatusAndOrderId(
                OVERDUE, rentalHistory.getOrder().getId());
        if (overdue) {
            notificationRepository.save(Notification.builder()
                    .member(rentalHistory.getMember())
                    .type(RENTAL_OVERDUE)
                    .message(RENTAL_OVERDUE.getDefaultMessage())
                    .build());
        }

        // 웹소켓 알림 전송
        NotifyDTO notifyDTO = NotifyDTO.of(RENTAL_OVERDUE, RENTAL_OVERDUE.getDefaultMessage());

        try {
            messagingTemplate.convertAndSend(
                    "/sub/member/" + rentalHistory.getMember().getId(), notifyDTO);
        } catch (Exception e) {
            log.warn("연체 알림 전송 실패: memberId={}, notifyDTO={}, 이유={}",
                    rentalHistory.getMember().getId(), notifyDTO, e.getMessage());
        }
    }

    @Override
    public void notifyRentalOverdueFee(RentalHistory rentalHistory, long overdueDays) {
        // 알림 저장
        RentalOverdueNotificationPolicy.fromDaysOverdue((int) overdueDays)
                .ifPresent(policy -> {
                    // 알림 중복 여부
                    if (!notificationRepository.existsByMemberAndRentalBookAndOverdueDay(
                            rentalHistory.getMember(),
                            rentalHistory.getRentalBook(),
                            policy.getOverdueDay())) {
                        // 알림 저장
                        notificationRepository.save(Notification.builder()
                                .member(rentalHistory.getMember())
                                .rentalBook(rentalHistory.getRentalBook())
                                .type(RENTAL_OVERDUE)
                                .message(policy.getMessage())
                                .overdueDay(policy.getOverdueDay())
                                .build());

                        // 웹소켓 알림 전송
                        NotifyDTO notifyDTO = NotifyDTO.of(RENTAL_OVERDUE, policy.getMessage());
                        try {
                            messagingTemplate.convertAndSend(
                                    "/sub/member/" + rentalHistory.getMember().getId(), notifyDTO);
                        } catch (Exception e) {
                            log.warn("이미 동일한 연체 알림 존재: memberId={}, bookId={}, overdueDay={}",
                                    rentalHistory.getMember().getId(),
                                    rentalHistory.getRentalBook().getId(),
                                    policy.getOverdueDay());
                        }
                    }
                });

    }

    @Override
    public void notifyReturnDueReminder(Order order, Member member,
                                        List<RentalHistory> rentalHistories,
                                        String message) {
        if (rentalHistories == null || rentalHistories.isEmpty()) return;

        boolean due = rentalHistoryRepository.existsByRentalStatusAndOrderId(
                RENTED, order.getId());
        if (due) return;

        // 알림 저장
        notificationRepository.save(Notification.builder()
                .member(member)
                .type(RETURN_DUE)
                .message(message)
                .build());

        // 웹소켓 알림 전송
        NotifyDTO notifyDTO = NotifyDTO.of(RETURN_DUE, message);
        try {
            messagingTemplate.convertAndSend(
                    "/sub/member/" + member.getId(), notifyDTO);
        } catch (Exception e) {
            log.warn("반납 예정일 알림 전송 실패: memberId={}, 이유={}",
                    member.getId(), e.getMessage());
        }
    }

    @Override
    public void notifyOrderShipped(Order order) {

    }

}
