package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.book.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.event.OverdueFeeEvent;
import com.uzem.book_cycle.event.RentalOverdueEvent;
import com.uzem.book_cycle.notification.type.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class OverdueServiceImplTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private OverduePolicy overduePolicy;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OverdueServiceImpl overdueService;

    @Test
    @DisplayName("연체처리 배치 성공")
    void processOverdue_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(RENTED)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(1))
                .rentalStatus(RENTED)
                .price(1000L)
                .rentalBook(rentalBook)
                .member(member)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        String message = NotificationType.RENTAL_OVERDUE.format("대여용 도서", 0);

        //when
        overdueService.processOverdue(rentalHistories);

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isNull();

        ArgumentCaptor<RentalOverdueEvent> captor = ArgumentCaptor.forClass(RentalOverdueEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());
        RentalOverdueEvent captured = captor.getValue();

        assertThat(captured.member()).isEqualTo(member);
        assertThat(captured.message()).isEqualTo(message);
        assertThat(captured.rentalHistories()).isEqualTo(rentalHistories);
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

        given(rentalHistoryRepository.findAllByRentalStatus(OVERDUE)).willReturn(rentalHistories);
        given(overduePolicy.calculateOverdue(overdueDays)).willReturn(0L);

        //when
        overdueService.processOverdueFees(LocalDate.now());

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isEqualTo(0L);

        ArgumentCaptor<OverdueFeeEvent> captor = ArgumentCaptor.forClass(OverdueFeeEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());
        OverdueFeeEvent captured = captor.getValue();

        assertThat(captured.rentalHistory()).isEqualTo(rentalHistory);
        assertThat(captured.overdueDays()).isEqualTo(overdueDays);
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
        overdueService.processOverdueFees(LocalDate.now());

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isEqualTo(1000L);

        ArgumentCaptor<OverdueFeeEvent> captor = ArgumentCaptor.forClass(OverdueFeeEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());
        OverdueFeeEvent captured = captor.getValue();

        assertThat(captured.rentalHistory()).isEqualTo(rentalHistory);
        assertThat(captured.overdueDays()).isEqualTo(overdueDays);
    }


    private static Member createMember() {
        return Member.builder()
                .id(1L)
                .rentalCnt(1)
                .build();
    }
}