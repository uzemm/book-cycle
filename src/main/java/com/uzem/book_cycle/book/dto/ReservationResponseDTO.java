package com.uzem.book_cycle.book.dto;

import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private Long reservationId;
    private LocalDateTime reservationDate;
    private Long rentalBookId;
    private Long memberId;
    private String title;
    private RentalStatus rentalStatus;
    private LocalDate paymentDeadline;

    public static ReservationResponseDTO from(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .reservationId(reservation.getId())
                .reservationDate(reservation.getCreatedAt())
                .memberId(reservation.getMember().getId())
                .rentalBookId(reservation.getRentalBook().getId())
                .title(reservation.getRentalBook().getTitle())
                .rentalStatus(reservation.getRentalBook().getRentalStatus())
                .paymentDeadline(reservation.getPaymentDeadline())
                .build();
    }

}
