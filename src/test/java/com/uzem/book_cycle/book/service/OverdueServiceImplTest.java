package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.notification.service.NotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class OverdueServiceImplTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private OverduePolicy overduePolicy;

    @Mock
    private NotificationServiceImpl notificationService;

    @InjectMocks
    private OverdueServiceImpl overdueService;

    @Test
    @DisplayName("연체처리 배치 성공")
    void updateStatusOverdue_success(){
        //given
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(1))
                .rentalStatus(RENTED)
                .price(1000L)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        given(rentalHistoryRepository.findAllByRentalStatus(RENTED)).willReturn(rentalHistories);

        //when
        overdueService.updateStatusOverdue(LocalDate.now());

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isNull();
        verify(notificationService, times(1)).notifyRentalOverdue(rentalHistory);
    }

    @Test
    @DisplayName("연체료 처리 배치 성공")
    void calculateOverdueFee_success(){
        //given
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(7))
                .rentalStatus(OVERDUE)
                .price(1000L)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        long overdueDays = ChronoUnit.DAYS.between(rentalHistory.getReturnDate(), LocalDate.now());
        System.out.println(overdueDays);
        given(rentalHistoryRepository.findAllByRentalStatus(OVERDUE)).willReturn(rentalHistories);
        given(overduePolicy.calculateOverdue(overdueDays)).willReturn(0L);

        //when
        overdueService.calculateOverdueFee(LocalDate.now());

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isEqualTo(0L);
        verify(notificationService, times(1)).notifyRentalOverdueFee(rentalHistory, overdueDays);
        verify(overduePolicy, times(1)).calculateOverdue(overdueDays);
    }

    @Test
    @DisplayName("연체료 처리 배치 성공 - 9일")
    void calculateOverdueFee_onD9_success(){
        //given
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(9))
                .rentalStatus(OVERDUE)
                .price(1000L)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        long overdueDays = ChronoUnit.DAYS.between(rentalHistory.getReturnDate(), LocalDate.now());
        given(rentalHistoryRepository.findAllByRentalStatus(OVERDUE)).willReturn(rentalHistories);
        given(overduePolicy.calculateOverdue(overdueDays)).willReturn(1000L);

        //when
        overdueService.calculateOverdueFee(LocalDate.now());

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isEqualTo(1000L);
        verify(notificationService, times(1)).notifyRentalOverdueFee(rentalHistory, overdueDays);
        verify(overduePolicy, times(1)).calculateOverdue(overdueDays);
    }


}