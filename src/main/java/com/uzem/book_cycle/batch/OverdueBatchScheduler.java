package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.book.service.OverdueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OverdueBatchScheduler {
    private final OverdueService overdueService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runOverdueBatch(){
        overdueService.updateStatusOverdue(LocalDate.now());
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void renOverdueFeeBatch(){
        overdueService.calculateOverdueFee(LocalDate.now());
    }
}
