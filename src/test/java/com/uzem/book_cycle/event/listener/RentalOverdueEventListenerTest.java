package com.uzem.book_cycle.event.listener;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.event.RentalReturnDueEvent;
import com.uzem.book_cycle.event.ReservationFirstEvent;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.dto.NotifyDTO;
import com.uzem.book_cycle.notification.entity.Notification;
import com.uzem.book_cycle.notification.repository.NotificationRepository;
import com.uzem.book_cycle.notification.type.NotificationType;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.event.OverdueFeeEvent;
import com.uzem.book_cycle.event.RentalOverdueEvent;
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
import java.util.List;
import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.notification.type.NotificationType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
class RentalOverdueEventListenerTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private RentalOverdueEventListener rentalOverdueEventListener;

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

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        String message = NotificationType.RENTAL_OVERDUE.format("대여용 도서", 0);
        given(rentalHistoryRepository.existsByRentalStatusAndOrderId(OVERDUE, order.getId()))
                .willReturn(true);
        RentalOverdueEvent event = new RentalOverdueEvent(member, rentalHistories, message);

        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        //when
        rentalOverdueEventListener.notifyRentalOverdue(event);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(RENTAL_OVERDUE);
        assertThat(saved.getMessage()).contains("도서의 반납일이 지나 연체가 발생했습니다. 빠른 반납 부탁드립니다.");
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

        OverdueFeeEvent event = new OverdueFeeEvent(rentalHistory, overdueDays);

        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        //when
        rentalOverdueEventListener.notifyRentalOverdueFee(event);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(RENTAL_OVERDUE);
        assertThat(saved.getMessage()).contains("반납일로부터 일주일이 지났습니다. 내일부터 연체료가 하루 500원씩 발생합니다.");
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

        OverdueFeeEvent event = new OverdueFeeEvent(rentalHistory, overdueDays);

        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        //when
        rentalOverdueEventListener.notifyRentalOverdueFee(event);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(RENTAL_OVERDUE);
        assertThat(saved.getMessage()).contains("연체가 길어지고 있어요! 최대 연체료에 도달하기 전에 꼭 반납해 주세요.");
    }

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
        String message = RESERVATION_FIRST.format("오만과 편견", 0);

        given(notificationRepository.existsByMemberAndRentalBookAndType(
                member, rentalBook, RESERVATION_FIRST))
                .willReturn(false);
        given(reservationRepository.
                findFirstByRentalBookAndRentalBook_RentalStatusAndIsActiveTrueOrderByReservationOrderAsc(
                        rentalBook, PENDING_PAYMENT))
                .willReturn(Optional.of(reservation));

        ReservationFirstEvent event = new ReservationFirstEvent(rentalBook, message);

        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        //when
        rentalOverdueEventListener.notifyNextReservationIfExists(event);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(RESERVATION_FIRST);
        assertThat(saved.getMessage()).contains("순번");
    }

    @Test
    @DisplayName("반납 예정일 알림 전송 성공")
    void notifyReturnDueReminder_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = getRentalBook_RENTED();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().plusDays(3))
                .rentalStatus(RENTED)
                .price(1000L)
                .member(member)
                .rentalBook(rentalBook)
                .order(order)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        String message = RETURN_DUE.format("오만과 편견", 0);

        given(notificationRepository.existsByMemberAndOrderAndType(
                member, order, RETURN_DUE))
                .willReturn(false);

        RentalReturnDueEvent event = new RentalReturnDueEvent(member, rentalHistories, message);

        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        //when
        rentalOverdueEventListener.notifyReturnDueReminder(event);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(RETURN_DUE);
        assertThat(saved.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("반납 예정일 알림 전송 성공 - 여러권 일 때")
    void notifyReturnDueReminder_withMultipleBooks_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = getRentalBook_RENTED();
        RentalBook rentalBook2 = RentalBook.builder()
                .id(2L)
                .title("싯다르타")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(RENTED)
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().plusDays(3))
                .rentalStatus(RENTED)
                .price(1000L)
                .member(member)
                .rentalBook(rentalBook)
                .order(order)
                .build();
        RentalHistory rentalHistory2 = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().plusDays(3))
                .rentalStatus(RENTED)
                .price(100L)
                .member(member)
                .rentalBook(rentalBook2)
                .order(order)
                .build();


        List<RentalHistory> rentalHistories = List.of(rentalHistory, rentalHistory2);
        String message = RETURN_DUE.format(rentalBook.getTitle(), rentalHistories.size() - 1);

        given(notificationRepository.existsByMemberAndOrderAndType(
                member, order, RETURN_DUE))
                .willReturn(false);

        RentalReturnDueEvent event = new RentalReturnDueEvent(member, rentalHistories, message);

        ArgumentCaptor<NotifyDTO> captor = ArgumentCaptor.forClass(NotifyDTO.class);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        //when
        rentalOverdueEventListener.notifyReturnDueReminder(event);

        //then
        verify(messagingTemplate).convertAndSend(eq("/sub/member/" + member.getId()), captor.capture());
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertThat(saved.getType()).isEqualTo(RETURN_DUE);
        assertThat(saved.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("알림 전송 실패 시 예외 캐치 후 로그만 남김")
    void notifyReturnDueReminder_fail(){
        //given
        Member member = createMember();
        RentalBook rentalBook = getRentalBook_RENTED();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().plusDays(3))
                .rentalStatus(RENTED)
                .price(1000L)
                .member(member)
                .rentalBook(rentalBook)
                .order(order)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        String message = RETURN_DUE.format("오만과 편견", 0);

        given(notificationRepository.existsByMemberAndOrderAndType(
                member, order, RETURN_DUE))
                .willReturn(false);

        doThrow(new RuntimeException("웹소켓 오류"))
                .when(messagingTemplate).convertAndSend(anyString(), Optional.ofNullable(any()));

        RentalReturnDueEvent event = new RentalReturnDueEvent(member, rentalHistories, message);


        //when
        rentalOverdueEventListener.notifyReturnDueReminder(event);

        //then
        verify(notificationRepository).save(any());

        verify(messagingTemplate).convertAndSend(anyString(), Optional.ofNullable(any()));
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

    private static RentalBook getRentalBook_RENTED() {
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("오만과 편견")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(RENTED)
                .build();
        return rentalBook;
    }
}