package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.uzem.book_cycle.order.type.CancelReason.AUTO_EXPIRED;
import static com.uzem.book_cycle.order.type.OrderStatus.PAID_READY;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OrderBatchScheduler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Transactional
    @Scheduled(cron = "0 0/10 * * * ?")
    public void cancelExpiredPaidReadyOrders(){ // PAID_READY
        LocalDateTime expired  = LocalDateTime.now().minusMinutes(30);

        List<Order> orders = orderRepository
                .findAllByOrderStatusAndCreatedAtBefore(PAID_READY, expired);

        for(Order order : orders){
            Member member = order.getMember();
            orderService.cancelOrderWithRestorationInNewTx(order, AUTO_EXPIRED);

            log.info("만료 주문 취소 완료: orderId={}, memberId={}, rentalCnt={}",
                    order.getId(), member.getId(), member.getRentalCnt());
        }
    }
}
