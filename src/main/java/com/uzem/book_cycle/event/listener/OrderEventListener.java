package com.uzem.book_cycle.event.listener;

import com.uzem.book_cycle.event.*;
import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.dto.NotifyDTO;
import com.uzem.book_cycle.notification.entity.Notification;
import com.uzem.book_cycle.notification.repository.NotificationRepository;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


import static com.uzem.book_cycle.notification.type.NotificationType.*;
import static com.uzem.book_cycle.order.type.OrderErrorCode.ORDER_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyOrderShipped(OrderShippedEvent event) {
        Order order = orderRepository.findById(event.orderId()).
                orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        Member member = order.getMember();

        String message = event.message();

        // 중복방지
        if (notificationRepository.existsByMemberAndOrderAndType(
                member, order, ORDER_SHIPPED)) return;

        notificationRepository.save(Notification.builder()
                .member(member)
                        .order(order)
                .type(ORDER_SHIPPED)
                .message(message)
                .build());

        // 웹소켓 알림 전송
        NotifyDTO notifyDTO = NotifyDTO.of(ORDER_SHIPPED, message);

        try {
            messagingTemplate.convertAndSend(
                    "/sub/member/" + member.getId(), notifyDTO);
        } catch (Exception e) {
            log.warn("주문 알림 전송 실패: memberId={}, orderId = {}, notifyDTO={}, 이유={}",
                    member.getId(), order.getId(), notifyDTO, e.getMessage());
        }
    }

}
