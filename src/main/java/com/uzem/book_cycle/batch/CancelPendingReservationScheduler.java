package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.book.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.PENDING_PAYMENT;

@Component
@RequiredArgsConstructor
public class CancelPendingReservationScheduler {

    private final RentalService rentalService;
    private final ReservationRepository reservationRepository;

    // 결제대기 기간 만료 배치
    @Scheduled(cron = "0 0 0 * * ?")
    public void runCancelExpiredReservationsBatch() {

        List<Reservation> reservations = reservationRepository
                .findAllByRentalBook_RentalStatusAndIsActiveTrueAndPaymentDeadlineBefore(
                        PENDING_PAYMENT, LocalDate.now());

        rentalService.updateCancelPendingPayment(reservations);
    }
}
