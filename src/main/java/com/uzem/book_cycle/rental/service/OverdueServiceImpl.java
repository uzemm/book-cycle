package com.uzem.book_cycle.rental.service;

import com.uzem.book_cycle.event.OverdueFeeEvent;
import com.uzem.book_cycle.event.RentalOverdueEvent;
import com.uzem.book_cycle.rental.entity.RentalHistory;
import com.uzem.book_cycle.rental.policy.OverduePolicy;
import com.uzem.book_cycle.rental.repository.RentalHistoryRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.type.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OverdueServiceImpl implements OverdueService{

    private final RentalHistoryRepository rentalHistoryRepository;
    private final OverduePolicy overduePolicy;
    private final ApplicationEventPublisher eventPublisher;


    // 연체 배치 처리 대여중 -> 연체
    @Override
    public void processOverdue(List<RentalHistory> rentalList) {

        Member member = rentalList.get(0).getMember();
        String title = rentalList.get(0).getRentalBook().getTitle();
        int other = rentalList.size() - 1;

        String message = NotificationType.RENTAL_OVERDUE.format(title, other);

        boolean hasOverdue = false;

        for(RentalHistory rentalHistory : rentalList) {
            try{
                    rentalHistory.statusOverdue(); // rented -> overdue
                    hasOverdue = true;
            } catch (Exception e){
                log.warn("자동 연체 처리 실패 - 대여이력ID: {}, 이유: {}",
                        rentalHistory.getId(), e.getMessage());
            }
        }
        // 연체 도서가 하나라도 있으면 알림 한 번만 전송
        if(hasOverdue) {
           eventPublisher.publishEvent(
                   new RentalOverdueEvent(
                           member, rentalList, message
                   )
           );
        }
    }

    @Override
    public void processOverdueFees(LocalDate today) {
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

    // 연체료 계산
    public long calculateOverdueFee(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        long overdueDays = ChronoUnit.DAYS.between(returnDate, now);

        return  overduePolicy.calculateOverdue(overdueDays);
    }

    // 연체료 알림 전송
    private void sendOverdueDaysNotification(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        long overdueDays = ChronoUnit.DAYS.between(returnDate, now);

        eventPublisher.publishEvent(
                new OverdueFeeEvent(
                        rentalHistory, overdueDays
                )
        );
    }
}
