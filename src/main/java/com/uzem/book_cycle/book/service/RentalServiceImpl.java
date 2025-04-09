package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final OverduePolicy overduePolicy;

    // 대여이력 생성
    public void createRentalHistory(RentalBook rentalBook, Member member, LocalDate now) {
        RentalHistory rentalHistory = RentalHistory.from(rentalBook, member, now);
        rentalHistoryRepository.save(rentalHistory);
    }

    // 연체 배치 처리
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateStatusOverdue() {
        LocalDate now = LocalDate.now();
        List<RentalHistory> rentalHistories = rentalHistoryRepository.findAllByRentalStatus(RENTED);

        for(RentalHistory rentalHistory : rentalHistories) {
            try{
                LocalDate returnDate = rentalHistory.getReturnDate();
                if(returnDate.isBefore(now)) {
                    rentalHistory.statusOverdue(); // rented -> overdue
                    long overdueFee = calculateOverdueFee(rentalHistory, now); // 연체료 계산
                    rentalHistory.setOverdueFee(overdueFee); // 연체료 저장
                }
            } catch (Exception e){
                log.warn("자동 연체 처리 실패 - 대여이력ID: {}, 이유: {}",
                        rentalHistory.getId(), e.getMessage());
            }

        }
    }

    // 연체료 계산
    @Override
    public long calculateOverdueFee(RentalHistory rentalHistory, LocalDate now) {
        LocalDate returnDate = rentalHistory.getReturnDate();
        long overdueDays = ChronoUnit.DAYS.between(returnDate, now); // 연체기간

        return  overduePolicy.calculateOverdue(rentalHistory, overdueDays);
    }


}
