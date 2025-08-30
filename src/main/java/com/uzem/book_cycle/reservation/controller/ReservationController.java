package com.uzem.book_cycle.reservation.controller;

import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.reservation.dto.ReservationRequestDTO;
import com.uzem.book_cycle.reservation.dto.ReservationResponseDTO;
import com.uzem.book_cycle.common.ApiResponse;
import com.uzem.book_cycle.reservation.service.ReservationService;
import com.uzem.book_cycle.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final AdminRentalRepository adminRentalRepository;

    // 도서 예약하기
    @PostMapping("/reservation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> createReservation(
            @RequestBody ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReservationResponseDTO reservation = reservationService.createReservation(
                requestDTO.getRentalBookId(), userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success(reservation));
    }

}
