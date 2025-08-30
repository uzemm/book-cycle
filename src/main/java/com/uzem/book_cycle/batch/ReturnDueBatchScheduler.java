package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.rental.entity.RentalHistory;
import com.uzem.book_cycle.rental.repository.RentalHistoryRepository;
import com.uzem.book_cycle.event.RentalReturnDueEvent;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.type.NotificationType;
import com.uzem.book_cycle.order.entity.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ReturnDueBatchScheduler {
    private final RentalHistoryRepository rentalHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 예약자 순번 알림 전송
    @Scheduled(cron = "0 0 11 * * ?")
    public void notifyReturnDueReminderIn3Days() {
        LocalDate dueDay = LocalDate.now().plusDays(3); // 반납 3일전

        List<RentalHistory> rentalHistories = rentalHistoryRepository
                .findAllByReturnDate(dueDay);

        Map<Order, List<RentalHistory>> group = rentalHistories.stream()
                .collect(Collectors.groupingBy(RentalHistory::getOrder)); // 주문번호로 묶음

        for(List<RentalHistory> rentalList : group.values()) {
            Member member = rentalList.get(0).getMember();
            String title = rentalList.get(0).getRentalBook().getTitle();
            int other = rentalList.size() - 1;
            String message = NotificationType.RETURN_DUE.format(title, other);

            eventPublisher.publishEvent(
                    new RentalReturnDueEvent(
                            member, rentalList, message
                    )
            );
        }
        log.info("반납 예정 알림: {}명에게 전송 완료(총 {}건)", group.size(), rentalHistories.size());
    }

}
