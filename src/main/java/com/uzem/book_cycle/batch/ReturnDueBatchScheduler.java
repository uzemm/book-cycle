package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.service.NotificationService;
import com.uzem.book_cycle.order.entity.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 11 * * ?")
    public void notifyReturnDueReminderIn3Days() {
        LocalDate dueDay = LocalDate.now().plusDays(3);

        List<RentalHistory> rentalHistories = rentalHistoryRepository
                .findAllByReturnDate(dueDay);

        Map<Order, List<RentalHistory>> group = rentalHistories.stream()
                .collect(Collectors.groupingBy(RentalHistory::getOrder));

        for(Map.Entry<Order, List<RentalHistory>> entry : group.entrySet()) {
            Order order = entry.getKey();
            List<RentalHistory> rentalList = entry.getValue();

            Member member = rentalList.get(0).getMember();
            String title = rentalList.get(0).getRentalBook().getTitle();
            int other = rentalList.size() - 1;

            String message = other == 0 ?
                    String.format("'%s'의 도서 반납 예정일이 다가왔습니다.", title) :
                    String.format("'%s'외 %d권의 도서 반납 예정일이 다가왔습니다.", title, other);

            notificationService.notifyReturnDueReminder(order, member, rentalList, message);
        }
        log.info("반납 예정 알림: {}명에게 전송 완료", group.size());
    }

}
