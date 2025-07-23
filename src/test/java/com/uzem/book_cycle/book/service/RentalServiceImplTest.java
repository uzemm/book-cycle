package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.dto.*;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.event.ReservationFirstEvent;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.payment.service.PaymentService;
import com.uzem.book_cycle.payment.type.PaymentPurpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("대여이력 생성 성공")
    void createRentalHistory_success(){
        //given
        Order order = Order.builder().id(2L).build();
        Member member = Member.builder().id(1L).build();
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .build();
        ArgumentCaptor<RentalHistory> captor = ArgumentCaptor.forClass(RentalHistory.class);

        //when
        rentalService.createRentalHistory(rentalBook, member, order, LocalDate.now());

        //then
        verify(rentalHistoryRepository, times(1))
                .save(captor.capture());
        assertEquals(RENTED, captor.getValue().getRentalStatus());
        assertEquals(order, captor.getValue().getOrder());
        assertEquals(member, captor.getValue().getMember());
        assertEquals(rentalBook, captor.getValue().getRentalBook());
    }

    @Test
    @DisplayName("결제대기기한 만료 시 취소처리 배치 성공")
    void cancelExpiredPendingReservation_success(){
        //given
        RentalBook rentalBook = RentalBook.builder()
                .rentalStatus(PENDING_PAYMENT)
                .build();
        Reservation reservation = Reservation.builder()
                .rentalBook(rentalBook)
                .paymentDeadline(LocalDate.now().minusDays(1))
                .isActive(true)
                .build();

        List<Reservation> reservationList = List.of(reservation);
        given(reservationRepository .findAllByRentalStatusAndPaymentDeadlineBefore(PENDING_PAYMENT, LocalDate.now()))
                .willReturn(reservationList);
        //when
        rentalService.updateCancelPendingPayment(reservationList);

        //then
        assertThat(rentalBook.getRentalStatus()).isEqualTo(AVAILABLE);
        assertThat(reservation.isActive()).isEqualTo(false);
        assertThat(reservation.getPaymentDeadline()).isNull();

    }

    @Test
    @DisplayName("결제대기기한 만료 시 취소처리 배치 성공 - 예약자있음 ")
    void cancelExpiredPendingReservations_andReorderReservation(){
        //given
        RentalBook rentalBook = RentalBook.builder()
                .rentalStatus(PENDING_PAYMENT)
                .reservations(new ArrayList<>())
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .paymentDeadline(LocalDate.now().minusDays(1))
                .reservationOrder(1)
                .isActive(true)
                .build();
        Reservation reservation2 = Reservation.builder()
                .id(2L)
                .reservationOrder(2)
                .paymentDeadline(null)
                .isActive(true)
                .build();

        rentalBook.addReservation(reservation);
        rentalBook.addReservation(reservation2);

        List<Reservation> reservationList = List.of(reservation);
        given(reservationRepository .findAllByRentalStatusAndPaymentDeadlineBefore(PENDING_PAYMENT, LocalDate.now()))
                .willReturn(reservationList);
        //when
        rentalService.updateCancelPendingPayment(reservationList);

        //then
        assertThat(rentalBook.getRentalStatus()).isEqualTo(PENDING_PAYMENT);
        assertThat(reservation.isActive()).isEqualTo(false);
        assertThat(reservation2.getReservationOrder()).isEqualTo(1);
        assertThat(reservation2.getPaymentDeadline()).isNotNull();

        ArgumentCaptor<ReservationFirstEvent> captor = ArgumentCaptor.forClass(ReservationFirstEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
    }

    @Test
    @DisplayName("반납처리 성공")
    void returnRental_success(){
        //given
        Member member = createMember();
        PaymentRequestDTO payment = getPaymentRequestDTO();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(5))
                .returnDate(LocalDate.now().plusDays(14))
                .rentalStatus(RENTED)
                .rentalBook(rentalBook)
                .member(member)
                .order(order)
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(rentalHistoryRepository.findAllByOrderId(order.getId())).willReturn(List.of(rentalHistory));

        //when
        GroupReturnResponseDTO groupReturnResponseDTO =
                rentalService.returnRental(rentalHistory.getOrder().getId(), member.getId(), payment);

        //then
        RentalHistoryResponseDTO response = groupReturnResponseDTO.getRentalHistory().get(0);
        assertThat(response.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(response.getActualReturnDate()).isEqualTo(LocalDate.now());
        assertThat(rentalBook.getRentalStatus()).isEqualTo(AVAILABLE);
        assertThat(member.getRentalCnt()).isEqualTo(0);
        assertThat(groupReturnResponseDTO).isNotNull();
    }

    @Test
    @DisplayName("연체도서 반납처리 성공")
    void returnOverdueRental_success(){
        //given
        Member member = createMember();
        PaymentRequestDTO paymentRequestDTO = getPaymentRequestDTO();
        PaymentResponseDTO paymentResponseDTO = getPaymentResponseDTO();
        RentalBook rentalBook1 = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .build();
        RentalBook rentalBook2 = RentalBook.builder()
                .title("대여용 도서")
                .price(2000L)
                .reservations(new ArrayList<>())
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory1 = getRentalHistory(order, rentalBook1, member);
        RentalHistory rentalHistory2 = getRentalHistory(order, rentalBook2, member);
        List<RentalHistory> rentalHistories = List.of(rentalHistory1, rentalHistory2);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(rentalHistoryRepository.findAllByOrderId(order.getId()))
                .willReturn(List.of(rentalHistory1, rentalHistory2));
        given(paymentService.processOverduePayment(paymentRequestDTO)).willReturn(paymentResponseDTO);

        //when
        GroupReturnResponseDTO groupReturnResponseDTO = rentalService.returnRental(
                rentalHistories.get(0).getOrder().getId(), member.getId(), paymentRequestDTO);

        //then
        assertThat(groupReturnResponseDTO).isNotNull();
        RentalHistoryResponseDTO result = groupReturnResponseDTO.getRentalHistory().get(0);
        assertThat(result).isNotNull();

        assertThat(rentalHistories.get(0).getRentalStatus()).isEqualTo(RETURNED);
        assertThat(rentalHistories.get(1).getRentalStatus()).isEqualTo(RETURNED);
        assertThat(result.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(result.getActualReturnDate()).isEqualTo(LocalDate.now());
        assertThat(rentalHistory1.isOverduePayment()).isEqualTo(true);

        assertThat(rentalBook1.getRentalStatus()).isEqualTo(AVAILABLE);
        assertThat(rentalBook1.getReservations()).isEmpty();

        assertThat(paymentRequestDTO.getAmount()).isEqualTo(2000L);
        assertThat(member.getRentalCnt()).isEqualTo(0);

        verify(paymentService, times(1)).processOverduePayment(any(PaymentRequestDTO.class));
    }

    private static RentalHistory getRentalHistory(Order order, RentalBook rentalBook, Member member) {
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(5))
                .returnDate(LocalDate.now().plusDays(14))
                .rentalStatus(RentalStatus.OVERDUE)
                .rentalBook(rentalBook)
                .member(member)
                .order(order)
                .overdueFee(1000L)
                .build();
        return rentalHistory;
    }

    @Test
    @DisplayName("연체도서 반납처리 - 예약자 있음")
    void returnOverdueRental_hasReservation_success(){
        //given
        Member member = createMember();
        PaymentRequestDTO paymentRequestDTO = getPaymentRequestDTO();
        PaymentResponseDTO paymentResponseDTO = getPaymentResponseDTO();
        Reservation reservation = Reservation.builder()
                .member(member)
                .reservationOrder(1)
                .paymentDeadline(null)
                .isActive(true)
                .build();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        rentalBook.addReservation(reservation);

        RentalHistory rentalHistory = getRentalHistory(order, rentalBook, member);
        List<RentalHistory> rentalHistories = List.of(rentalHistory);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(rentalHistoryRepository.findAllByOrderId(order.getId()))
                .willReturn(List.of(rentalHistory));
        given(paymentService.processOverduePayment(paymentRequestDTO)).willReturn(paymentResponseDTO);
        given(reservationRepository.findFirstByRentalBookAndRentalBook_RentalStatusAndIsActiveTrueOrderByReservationOrderAsc(rentalBook, PENDING_PAYMENT))
                .willReturn(Optional.of(reservation));

        //when
        GroupReturnResponseDTO groupReturnResponseDTO = rentalService.returnRental(
                rentalHistories.get(0).getOrder().getId(), member.getId(), paymentRequestDTO);

        //then
        RentalHistoryResponseDTO result = groupReturnResponseDTO.getRentalHistory().get(0);
        assertThat(result.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(rentalHistory.isOverduePayment()).isEqualTo(true);

        assertThat(rentalBook.getRentalStatus()).isEqualTo(PENDING_PAYMENT);
        assertThat(rentalBook.getReservations().size()).isEqualTo(1);

        assertThat(reservation.getPaymentDeadline()).isNotNull();
        assertThat(reservation.getPaymentDeadline()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(reservation.getReservationOrder()).isEqualTo(1);

        verify(paymentService, times(1)).processOverduePayment(
                any(PaymentRequestDTO.class));

        ArgumentCaptor<ReservationFirstEvent> captor = ArgumentCaptor.forClass(ReservationFirstEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
    }

    @Test
    @DisplayName("대여현황조회 성공")
    void getMyRentals_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservations(Collections.emptyList())
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = getRentalHistoryRented(order, rentalBook, member);

        given(rentalHistoryRepository.findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(RENTED, member.getId()))
                .willReturn(List.of(rentalHistory));

        //when
        List<RentalHistoryResponseDTO> myRentals = rentalService.getMyRentals(member.getId());

        //then
        assertThat(myRentals).isNotNull();
        assertThat(myRentals.size()).isEqualTo(1);
        assertThat(myRentals.get(0).getRentalStatus()).isEqualTo(RENTED);
        assertThat(myRentals.get(0).getOrderId()).isEqualTo(1);
    }

    @Test
    @DisplayName("연체현황조회 성공")
    void getMyOverdue_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservations(Collections.emptyList())
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory = getRentalHistory(order, rentalBook, member);

        given(rentalHistoryRepository.findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(OVERDUE, member.getId()))
                .willReturn(List.of(rentalHistory));

        //when
        List<OverdueListResponseDTO> myOverdue = rentalService.getMyOverdue(member.getId());

        //then
        assertThat(myOverdue).isNotNull();
        assertThat(myOverdue.get(0).getOrderId()).isEqualTo(1);
        assertThat(myOverdue.get(0).getCount()).isEqualTo(1);
        assertThat(myOverdue.get(0).getTotalOverdueFee()).isEqualTo(1000);
    }

    @Test
    @DisplayName("대여이력조회 성공")
    void getMyRentalHistories_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        PaymentResponseDTO paymentResponseDTO = getPaymentResponseDTO();
        RentalHistory rentalHistory = getRentalHistoryReturned(order, rentalBook, member);

        given(rentalHistoryRepository.findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(RETURNED, member.getId()))
                .willReturn(List.of(rentalHistory));
        given(paymentService.getOverduePayment(rentalHistory.getOrder())).willReturn(paymentResponseDTO);

        //when
        List<RentalHistoryListResponseDTO> myRentalHistories = rentalService.getMyRentalHistories(member.getId());

        //then
        assertThat(myRentalHistories).isNotNull();
        assertThat(myRentalHistories.get(0).getOrderId()).isNotNull();
        assertThat(myRentalHistories.get(0).getCount()).isEqualTo(1);
        assertThat(myRentalHistories.get(0).getTotalOverdueFee()).isEqualTo(3000);
        assertThat(myRentalHistories.get(0).getRentalHistoryList().get(0).getPayment()).isNotNull();
    }

    @Test
    @DisplayName("예약하기 성공")
    void createReservation_success(){
        //given
        Member member = createMember();
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(RENTED)
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        //when
        ReservationResponseDTO reservation = rentalService.createReservation(rentalBook, member.getId());

        //then
        assertThat(reservation).isNotNull();
        assertThat(reservation.getReservationOrder()).isEqualTo(1);
        assertThat(reservation.isActive()).isEqualTo(true);
        assertThat(reservation.getPaymentDeadline()).isNull();
    }

    @Test
    @DisplayName("예약하기 성공 - 순번 2번째")
    void createReservation_whenSecond_thenSuccess(){
        //given
        Member member = createMember();
        Member member2 = Member.builder()
                .id(2L)
                .rentalCnt(1)
                .build();
        Reservation reservation1 = Reservation.builder()
                .id(1L)
                .member(member)
                .reservationOrder(1)
                .paymentDeadline(null)
                .isActive(true)
                .build();
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(RENTED)
                .build();
        rentalBook.addReservation(reservation1);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(memberRepository.findById(2L)).willReturn(Optional.of(member2));
        given(reservationRepository.existsByRentalBookAndMemberAndIsActiveTrue(rentalBook, member)).willReturn(true);

        //when
        ReservationResponseDTO result = rentalService.createReservation(rentalBook, member2.getId());

        //then
        assertThat(result.getReservationOrder()).isEqualTo(2);
        assertThat(rentalBook.getReservations().size()).isEqualTo(2);
        assertThat(rentalBook.getReservations().get(0).getReservationOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("예약 취소 - 순번 1 예약자 취소")
    void cancelReservation_andReorderReservation(){
        //given
        Member member = createMember();
        Member member2 = Member.builder()
                .id(2L)
                .rentalCnt(1)
                .build();
        Reservation reservation1 = Reservation.builder()
                .id(1L)
                .member(member)
                .reservationOrder(1)
                .paymentDeadline(null)
                .isActive(true)
                .build();
        Reservation reservation2 = Reservation.builder()
                .id(2L)
                .member(member2)
                .reservationOrder(2)
                .paymentDeadline(null)
                .isActive(true)
                .build();
        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .reservations(new ArrayList<>())
                .rentalStatus(RENTED)
                .build();
        rentalBook.addReservation(reservation1);
        rentalBook.addReservation(reservation2);
        given(reservationRepository.findByRentalBookAndMemberIdAndIsActiveTrue(rentalBook, member.getId()))
                .willReturn(Optional.of(reservation1));
        given(reservationRepository.findFirstByRentalBookAndRentalBook_RentalStatusAndIsActiveTrueOrderByReservationOrderAsc(rentalBook, PENDING_PAYMENT))
                .willReturn(Optional.of(reservation2));

        //when
        rentalService.cancelMyReservation(rentalBook, member.getId());

        //then
        assertThat(reservation2.getReservationOrder()).isEqualTo(1);
        assertThat(reservation1.getReservationOrder()).isEqualTo(0);
        assertThat(reservation1.isActive()).isEqualTo(false);

        ArgumentCaptor<ReservationFirstEvent> captor = ArgumentCaptor.forClass(ReservationFirstEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
    }

    private static RentalHistory getRentalHistoryRented(Order order, RentalBook rentalBook, Member member) {
        return RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(5))
                .returnDate(LocalDate.now().plusDays(14))
                .rentalStatus(RENTED)
                .rentalBook(rentalBook)
                .member(member)
                .order(order)
                .overdueFee(1000L)
                .build();
    }

    private static RentalHistory getRentalHistoryReturned(Order order, RentalBook rentalBook, Member member) {
        return RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(5))
                .returnDate(LocalDate.now().plusDays(14))
                .rentalStatus(RETURNED)
                .rentalBook(rentalBook)
                .member(member)
                .order(order)
                .overdueFee(1000L)
                .isOverduePayment(true)
                .build();
    }

    private static Member createMember() {
        return Member.builder()
                .id(1L)
                .rentalCnt(1)
                .build();
    }

    private static PaymentRequestDTO getPaymentRequestDTO() {
        return PaymentRequestDTO.builder()
                .amount(3000L)
                .build();
    }

    private static PaymentResponseDTO getPaymentResponseDTO() {
        return PaymentResponseDTO.builder()
                .amount(3000L)
                .paymentPurpose(PaymentPurpose.OVERDUE)
                .build();
    }
}