package com.uzem.book_cycle.notification.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.dto.NotifyDTO;
import com.uzem.book_cycle.notification.repository.NotificationRepository;
import com.uzem.book_cycle.order.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.notification.type.NotificationType.RENTAL_OVERDUE;
import static com.uzem.book_cycle.notification.type.NotificationType.RESERVATION_FIRST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class NotificationServiceImplTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("예약 순번 알림 전송 성공")
    void notifyNextReservationIfExists_success(){
        //given
        Member member = createMember();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .member(member)
                .reservationOrder(1)
                .paymentDeadline(null)
                .isActive(true)
                .build();
        RentalBook rentalBook = getRentalBook_PENDING_PAYMENT();
        rentalBook.addReservation(reservation);
        given(reservationRepository.existsByMemberAndRentalBookAndPaymentDeadline(
                member, rentalBook, reservation.getPaymentDeadline()))
                .willReturn(false);
        given(reservationRepository.
                findFirstByRentalBookAndRentalBook_RentalStatusAndIsActiveTrueOrderByReservationOrderAsc(
                        rentalBook, PENDING_PAYMENT))
                .willReturn(Optional.of(reservation));
        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);

        //when
        notificationService.notifyNextReservationIfExists(rentalBook);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());

        NotifyDTO dto = captor.getValue();
        assertThat(dto.getType()).isEqualTo(RESERVATION_FIRST);
        assertThat(dto.getMessage()).contains("예약");
    }

    @Test
    @DisplayName("연체 시 알림 전송 성공")
    void notifyRentalOverdue_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = getRentalBook_OVERDUE();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(1))
                .rentalStatus(OVERDUE)
                .price(1000L)
                .member(member)
                .rentalBook(rentalBook)
                .order(order)
                .build();

        given(rentalHistoryRepository.existsByRentalStatusAndOrderId(OVERDUE, order.getId()))
                .willReturn(false);
        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);

        //when
        notificationService.notifyRentalOverdue(rentalHistory);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());

        NotifyDTO dto = captor.getValue();
        assertThat(dto.getType()).isEqualTo(RENTAL_OVERDUE);
        assertThat(dto.getMessage()).contains("연체");

    }

    @Test
    @DisplayName("연체료 알림 전송 배치 성공 - 7일 ")
    void notifyRentalOverdueFee_onD7_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = getRentalBook_OVERDUE();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(7))
                .rentalStatus(OVERDUE)
                .price(1000L)
                .member(member)
                .rentalBook(rentalBook)
                .order(order)
                .build();

        long overdueDays = ChronoUnit.DAYS.between(rentalHistory.getReturnDate(), LocalDate.now());
        given(notificationRepository.existsByMemberAndRentalBookAndOverdueDay(member, rentalBook, (int)overdueDays))
                .willReturn(false);
        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);

        //when
        notificationService.notifyRentalOverdueFee(rentalHistory, overdueDays);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());

        NotifyDTO dto = captor.getValue();

        assertThat(dto.getType()).isEqualTo(RENTAL_OVERDUE);
        assertThat(dto.getMessage()).contains("반납일로부터 일주일이 지났습니다. 내일부터 연체료가 하루 500원씩 발생합니다.");
    }

    @Test
    @DisplayName("연체료 알림 전송 성공 - 17일")
    void notifyRentalOverdueFee_onD17_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = getRentalBook_OVERDUE();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(17))
                .rentalStatus(OVERDUE)
                .price(1000L)
                .member(member)
                .rentalBook(rentalBook)
                .order(order)
                .build();

        long overdueDays = ChronoUnit.DAYS.between(rentalHistory.getReturnDate(), LocalDate.now());
        given(notificationRepository.existsByMemberAndRentalBookAndOverdueDay(member, rentalBook, (int)overdueDays))
                .willReturn(false);
        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);

        //when
        notificationService.notifyRentalOverdueFee(rentalHistory, overdueDays);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/"+member.getId()), captor.capture());

        NotifyDTO dto = captor.getValue();
        assertThat(dto.getType()).isEqualTo(RENTAL_OVERDUE);
        assertThat(dto.getMessage()).contains("연체");
    }

    private static Member createMember() {
        return Member.builder()
                .id(1L)
                .rentalCnt(1)
                .build();
    }

    private static RentalBook getRentalBook_PENDING_PAYMENT() {
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("오만과 편견")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(PENDING_PAYMENT)
                .build();
        return rentalBook;
    }

    private static RentalBook getRentalBook_OVERDUE() {
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(OVERDUE)
                .build();
        return rentalBook;
    }

}