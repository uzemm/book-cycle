package com.uzem.book_cycle.reservation.service;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.reservation.dto.ReservationResponseDTO;
import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.event.ReservationFirstEvent;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.reservation.entity.Reservation;
import com.uzem.book_cycle.reservation.repository.ReservationRepository;
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
import java.util.List;
import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AdminRentalRepository adminRentalRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ReservationServiceImpl reservationService;

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
        given(reservationRepository
                .findAllByRentalBook_RentalStatusAndIsActiveTrueAndPaymentDeadlineBefore(
                        PENDING_PAYMENT, LocalDate.now()))
                .willReturn(reservationList);
        //when
        reservationService.updateCancelPendingPayment(reservationList);

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
        given(reservationRepository
                .findAllByRentalBook_RentalStatusAndIsActiveTrueAndPaymentDeadlineBefore(
                        PENDING_PAYMENT, LocalDate.now()))
                .willReturn(reservationList);
        //when
        reservationService.updateCancelPendingPayment(reservationList);

        //then
        assertThat(rentalBook.getRentalStatus()).isEqualTo(PENDING_PAYMENT);
        assertThat(reservation.isActive()).isEqualTo(false);
        assertThat(reservation2.getReservationOrder()).isEqualTo(1);
        assertThat(reservation2.getPaymentDeadline()).isNotNull();

        ArgumentCaptor<ReservationFirstEvent> captor = ArgumentCaptor.forClass(ReservationFirstEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
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

        given(adminRentalRepository.findById(1L)).willReturn(Optional.of(rentalBook));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        //when
        ReservationResponseDTO reservation = reservationService.createReservation(
                rentalBook.getId(), member.getId());

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
        given(adminRentalRepository.findById(1L)).willReturn(Optional.of(rentalBook));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(memberRepository.findById(2L)).willReturn(Optional.of(member2));
        given(reservationRepository.existsByRentalBookAndMemberAndIsActiveTrue(rentalBook, member)).willReturn(true);

        //when
        ReservationResponseDTO result = reservationService.createReservation(
                rentalBook.getId(), member2.getId());

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
        reservationService.cancelMyReservation(rentalBook, member.getId());

        //then
        assertThat(reservation2.getReservationOrder()).isEqualTo(1);
        assertThat(reservation1.getReservationOrder()).isEqualTo(0);
        assertThat(reservation1.isActive()).isEqualTo(false);

        ArgumentCaptor<ReservationFirstEvent> captor = ArgumentCaptor.forClass(ReservationFirstEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
    }

    private static Member createMember() {
        return Member.builder()
                .id(1L)
                .rentalCnt(1)
                .build();
    }

}