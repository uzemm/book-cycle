package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.dto.GroupReturnResponseDTO;
import com.uzem.book_cycle.book.dto.RentalHistoryResponseDTO;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.payment.repository.PaymentRepository;
import com.uzem.book_cycle.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.payment.type.PaymentPurpose.OVERDUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class RentalServiceImplTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private OverduePolicy overduePolicy;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("대여이력 생성 성공")
    void createRentalHistory(){
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
    @DisplayName("연체처리 배치 성공")
    void successOverdueBatch(){
        //given
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(20))
                .returnDate(LocalDate.now().minusDays(6))
                .rentalStatus(RENTED)
                .price(1000L)
                .build();

        List<RentalHistory> rentalHistories = List.of(rentalHistory);
        given(rentalHistoryRepository.findAllByRentalStatus(RENTED)).willReturn(rentalHistories);
        given(overduePolicy.calculateOverdue(any(), anyLong())).willReturn(6000L);

        //when
        rentalService.updateStatusOverdue();

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isEqualTo(6000L);
        verify(overduePolicy, times(1)).calculateOverdue(rentalHistory, 6);
    }

    @Test
    @DisplayName("결제대기기한 만료 시 취소처리 배치 성공")
    void successUpdatePendingPayment(){
        //given
        RentalBook rentalBook = RentalBook.builder()
                .rentalStatus(PENDING_PAYMENT)
                .build();
        Reservation reservation = Reservation.builder()
                .rentalBook(rentalBook)
                .paymentDeadline(LocalDate.now().minusDays(1))
                .build();

        List<Reservation> reservationList = List.of(reservation);
        given(reservationRepository.findAllByRentalBook_RentalStatus(PENDING_PAYMENT))
                .willReturn(reservationList);
        //when
        rentalService.updateCancelPendingPayment();

        //then
        assertThat(rentalBook.getRentalStatus()).isEqualTo(AVAILABLE);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    @DisplayName("반납처리 성공")
    void successReturnRental(){
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
                .reservation(null)
                .build();
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(5))
                .returnDate(LocalDate.now().plusDays(14))
                .rentalStatus(RENTED)
                .rentalBook(rentalBook)
                .member(member)
                .order(order)
                .build();

        given(rentalHistoryRepository.findAllByOrderId(order.getId())).willReturn(List.of(rentalHistory));

        //when
        GroupReturnResponseDTO groupReturnResponseDTO =
                rentalService.returnRental(rentalHistory.getOrder().getId(), member, payment);

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
    void successReturnOverdueRental(){
        //given
        Member member = createMember();
        PaymentRequestDTO paymentRequestDTO = getPaymentRequestDTO();
        PaymentResponseDTO paymentResponseDTO = getPaymentResponseDTO();
        RentalBook rentalBook1 = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservation(null)
                .build();
        RentalBook rentalBook2 = RentalBook.builder()
                .title("대여용 도서")
                .price(2000L)
                .reservation(null)
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        RentalHistory rentalHistory1 = getRentalHistory(order, rentalBook1, member);
        RentalHistory rentalHistory2 = getRentalHistory(order, rentalBook2, member);
        List<RentalHistory> rentalHistories = List.of(rentalHistory1, rentalHistory2);

        given(rentalHistoryRepository.findAllByOrderId(order.getId()))
                .willReturn(List.of(rentalHistory1, rentalHistory2));
        given(paymentService.processOverduePayment(paymentRequestDTO)).willReturn(paymentResponseDTO);

        //when
        GroupReturnResponseDTO groupReturnResponseDTO = rentalService.returnRental(
                rentalHistories.get(0).getOrder().getId(), member, paymentRequestDTO);

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
        assertThat(rentalBook1.getReservation()).isNull();

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
    @DisplayName("연체도서 반납처리 - 예약자 있을 때")
    void successReturnOverdueRental_hasReservation(){
        //given
        Member member = createMember();
        PaymentRequestDTO paymentRequestDTO = getPaymentRequestDTO();
        PaymentResponseDTO paymentResponseDTO = getPaymentResponseDTO();
        Reservation reservation = Reservation.builder()
                .member(member)
                .paymentDeadline(null)
                .build();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservation(null)
                .build();
        Order order = Order.builder()
                .id(1L)
                .build();
        reservation.setRentalBook(rentalBook);

        RentalHistory rentalHistory = getRentalHistory(order, rentalBook, member);
        List<RentalHistory> rentalHistories = List.of(rentalHistory);

        given(rentalHistoryRepository.findAllByOrderId(order.getId()))
                .willReturn(List.of(rentalHistory));
        given(paymentService.processOverduePayment(paymentRequestDTO)).willReturn(paymentResponseDTO);

        //when
        GroupReturnResponseDTO groupReturnResponseDTO = rentalService.returnRental(
                rentalHistories.get(0).getOrder().getId(), member, paymentRequestDTO);

        //then
        RentalHistoryResponseDTO result = groupReturnResponseDTO.getRentalHistory().get(0);
        assertThat(result.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(rentalHistory.isOverduePayment()).isEqualTo(true);

        assertThat(rentalBook.getRentalStatus()).isEqualTo(PENDING_PAYMENT);

        assertThat(reservation.getPaymentDeadline()).isNotNull();
        assertThat(reservation.getPaymentDeadline()).isEqualTo(LocalDate.now().plusDays(1));

        verify(paymentService, times(1)).processOverduePayment(any(PaymentRequestDTO.class));
    }

    private static Member createMember() {
        Member member = Member.builder()
                .id(1L)
                .rentalCnt(1)
                .build();
        return member;
    }

    private static PaymentRequestDTO getPaymentRequestDTO() {
        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .amount(3000L)
                .build();
        return payment;
    }

    private static PaymentResponseDTO getPaymentResponseDTO() {
        PaymentResponseDTO payment = PaymentResponseDTO.builder()
                .amount(3000L)
                .paymentPurpose(OVERDUE)
                .build();
        return payment;
    }
}