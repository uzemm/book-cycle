package com.uzem.book_cycle.book.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.dto.RentalHistoryResponseDTO;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.policy.OverduePolicy;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @InjectMocks
    private RentalServiceImpl rentalService;

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

        List<RentalHistory> rentalHistoryLIST = List.of(rentalHistory);
        given(rentalHistoryRepository.findAllByRentalStatus(RENTED)).willReturn(rentalHistoryLIST);
        given(overduePolicy.calculateOverdue(any(), anyLong())).willReturn(6000L);

        //when
        rentalService.updateStatusOverdue();

        //then
        assertThat(rentalHistory.getRentalStatus()).isEqualTo(OVERDUE);
        assertThat(rentalHistory.getOverdueFee()).isEqualTo(6000L);
        verify(overduePolicy).calculateOverdue(rentalHistory, 6);
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
        verify(reservationRepository).delete(reservation);
    }

    @Test
    @DisplayName("반납처리 성공")
    void successReturnRental(){
        //given
        Member member = createMember();
        PaymentRequestDTO payment = getPaymentRequestDTO();
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
                .build();

        //when
        RentalHistoryResponseDTO rentalHistoryResponseDTO =
                rentalService.returnRental(member, payment, rentalHistory);

        //then
        assertThat(rentalHistoryResponseDTO).isNotNull();
        assertThat(rentalHistoryResponseDTO.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(rentalHistoryResponseDTO.getActualReturnDate()).isEqualTo(LocalDate.now());
        assertThat(rentalBook.getRentalStatus()).isEqualTo(AVAILABLE);
        assertThat(member.getRentalCnt()).isEqualTo(0);
    }

    @Test
    @DisplayName("연체도서 반납처리 성공")
    void successReturnOverdueRental(){
        //given
        Member member = createMember();
        PaymentRequestDTO payment = getPaymentRequestDTO();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservation(null)
                .build();
        RentalHistory rentalHistory = getRentalHistory(rentalBook, member);

        //when
        RentalHistoryResponseDTO rentalHistoryResponseDTO =
                rentalService.returnRental(member, payment, rentalHistory);

        //then
        assertThat(rentalHistoryResponseDTO).isNotNull();
        assertThat(rentalHistoryResponseDTO.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(rentalHistoryResponseDTO.getActualReturnDate()).isEqualTo(LocalDate.now());
        assertThat(rentalHistory.isOverduePayment()).isEqualTo(true);
        assertThat(rentalBook.getRentalStatus()).isEqualTo(AVAILABLE);
        assertThat(member.getRentalCnt()).isEqualTo(0);
        assertThat(rentalBook.getReservation()).isNull();

        verify(paymentService, times(1)).processPayment(any(PaymentRequestDTO.class));
    }

    private static RentalHistory getRentalHistory(RentalBook rentalBook, Member member) {
        RentalHistory rentalHistory = RentalHistory.builder()
                .rentalDate(LocalDate.now().minusDays(5))
                .returnDate(LocalDate.now().plusDays(14))
                .rentalStatus(OVERDUE)
                .rentalBook(rentalBook)
                .member(member)
                .build();
        return rentalHistory;
    }

    @Test
    @DisplayName("연체도서 반납처리 - 예약자 있을 때")
    void successReturnOverdueRental_hasReservation(){
        //given
        Member member = createMember();
        PaymentRequestDTO payment = getPaymentRequestDTO();
        Reservation reservation = Reservation.builder()
                .member(member)
                .paymentDeadline(null)
                .build();
        RentalBook rentalBook = RentalBook.builder()
                .title("대여용 도서")
                .price(1000L)
                .reservation(reservation)
                .rentalStatus(OVERDUE)
                .build();
        reservation.setRentalBook(rentalBook);
        RentalHistory rentalHistory = getRentalHistory(rentalBook, member);

        //when
        RentalHistoryResponseDTO rentalHistoryResponseDTO =
                rentalService.returnRental(member, payment, rentalHistory);

        //then
        assertThat(rentalHistoryResponseDTO.getRentalStatus()).isEqualTo(RETURNED);
        assertThat(rentalHistory.isOverduePayment()).isEqualTo(true);
        assertThat(rentalBook.getRentalStatus()).isEqualTo(PENDING_PAYMENT);
        assertThat(reservation.getPaymentDeadline()).isNotNull();
        assertThat(reservation.getPaymentDeadline()).isEqualTo(LocalDate.now().plusDays(1));

        verify(paymentService, times(1)).processPayment(any(PaymentRequestDTO.class));
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
                .build();
        return payment;
    }
}