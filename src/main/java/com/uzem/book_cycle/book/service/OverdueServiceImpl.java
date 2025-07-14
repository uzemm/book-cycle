package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OverdueServiceImpl implements OverdueService{

    private final RentalHistoryRepository rentalHistoryRepository;
    private final NotificationService notificationService;
    private final OverduePolicy overduePolicy;


    // 연체 배치 처리 대여중 -> 연체
    @Override
    public void updateStatusOverdue(LocalDate today) {
        List<RentalHistory> rentalHistories = rentalHistoryRepository.findAllByRentalStatus(RENTED);

        for(RentalHistory rentalHistory : rentalHistories) {
            try{
                processOverdue(rentalHistory, today);
            } catch (Exception e){
                log.warn("자동 연체 처리 실패 - 대여이력ID: {}, 이유: {}",
                        rentalHistory.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void calculateOverdueFee(LocalDate today) {
        List<RentalHistory> overdueList = rentalHistoryRepository.findAllByRentalStatus(OVERDUE);

        for(RentalHistory rentalHistory : overdueList) {
            try{
                long overdueFee = calculateOverdueFee(rentalHistory, today); // 연체료 계산
                rentalHistory.setOverdueFee(overdueFee); // 연체료 저장
                sendOverdueDaysNotification(rentalHistory, today); // 알림 전송
            } catch (Exception e){
                log.warn("자동 연체료 계산 처리 실패 - 대여이력ID: {}, 이유: {}",
                        rentalHistory.getId(), e.getMessage());
            }
        }
    }

    private void processOverdue(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        if(returnDate.isBefore(now)) {
            rentalHistory.statusOverdue(); // rented -> overdue
            sendOverdueNotification(rentalHistory); // 알림 전송
        }
    }

    // 연체료 계산
    public long calculateOverdueFee(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        long overdueDays = ChronoUnit.DAYS.between(returnDate, now);

        return  overduePolicy.calculateOverdue(overdueDays);
    }

    private void sendOverdueNotification(RentalHistory rentalHistory) {
        notificationService.notifyRentalOverdue(rentalHistory);
    }

    // 연체료 알림 전송
    private void sendOverdueDaysNotification(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        long overdueDays = ChronoUnit.DAYS.between(returnDate, now);
        notificationService.notifyRentalOverdueFee(rentalHistory, overdueDays);
    }
}
