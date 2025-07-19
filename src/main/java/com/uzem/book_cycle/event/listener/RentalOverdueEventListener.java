package com.uzem.book_cycle.event.listener;

import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.event.OverdueFeeEvent;
import com.uzem.book_cycle.event.RentalOverdueEvent;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.dto.NotifyDTO;
import com.uzem.book_cycle.notification.entity.Notification;
import com.uzem.book_cycle.notification.repository.NotificationRepository;
import com.uzem.book_cycle.notification.type.RentalOverdueNotificationPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static com.uzem.book_cycle.notification.type.NotificationType.RENTAL_OVERDUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RentalOverdueEventListener {
    private final NotificationRepository notificationRepository;
    private final RentalHistoryRepository rentalHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyRentalOverdue(RentalOverdueEvent event) {
        Member member = event.getMember();
        String message = event.getMessage();
        RentalHistory rentalHistory = event.getRentalHistories().get(0);

        // 알림 저장
        boolean overdue = rentalHistoryRepository.existsByRentalStatusAndOrderId(
                OVERDUE, rentalHistory.getOrder().getId());
        if (overdue) {
            notificationRepository.save(Notification.builder()
                    .member(member)
                    .type(RENTAL_OVERDUE)
                    .message(message)
                    .build());
        }

        // 웹소켓 알림 전송
        NotifyDTO notifyDTO = NotifyDTO.of(RENTAL_OVERDUE, message);

        try {
            messagingTemplate.convertAndSend(
                    "/sub/member/" + member.getId(), notifyDTO);
        } catch (Exception e) {
            log.warn("연체 알림 전송 실패: memberId={}, notifyDTO={}, 이유={}",
                    member.getId(), notifyDTO, e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyRentalOverdueFee(OverdueFeeEvent event) {
        RentalHistory rentalHistory = event.getRentalHistory();

        Optional<RentalOverdueNotificationPolicy> optionalPolicy
                = RentalOverdueNotificationPolicy.fromDaysOverdue((int) event.getOverdueDays());

        if(optionalPolicy .isEmpty()) return;

        RentalOverdueNotificationPolicy policy = optionalPolicy.get();
        if (notificationRepository.existsByMemberAndRentalBookAndOverdueDay(
                rentalHistory.getMember(),
                rentalHistory.getRentalBook(),
                policy.getOverdueDay()))
            return;

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
            log.warn("연체료 알림 전송 실패: memberId={}, bookId={}, overdueDay={}",
                    rentalHistory.getMember().getId(),
                    rentalHistory.getRentalBook().getId(),
                    policy.getOverdueDay());
        }
    }
}
