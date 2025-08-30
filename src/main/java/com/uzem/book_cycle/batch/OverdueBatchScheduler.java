package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.rental.entity.RentalHistory;
import com.uzem.book_cycle.rental.repository.RentalHistoryRepository;
import com.uzem.book_cycle.rental.service.OverdueService;
import com.uzem.book_cycle.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;

@Component
@RequiredArgsConstructor
public class OverdueBatchScheduler {
    private final OverdueService overdueService;
    private final RentalHistoryRepository rentalHistoryRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runOverdueBatch() {

        List<RentalHistory> rentalHistories = rentalHistoryRepository
                .findAllByRentalStatusAndReturnDateBefore(RENTED, LocalDate.now());

        Map<Order, List<RentalHistory>> group = rentalHistories.stream()
                .collect(Collectors.groupingBy(RentalHistory::getOrder));

        for(List<RentalHistory> rentalList : group.values()) {
            overdueService.processOverdue(rentalList);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void runOverdueFeeBatch(){
        overdueService.processOverdueFees(LocalDate.now());
    }
}
