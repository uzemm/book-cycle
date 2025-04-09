package com.uzem.book_cycle.book.controller;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.book.dto.ReservationRequestDTO;
import com.uzem.book_cycle.book.dto.ReservationResponseDTO;
import com.uzem.book_cycle.book.service.RentalService;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.RENTAL_BOOK_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final AdminRentalRepository adminRentalRepository;

    // 도서 예약하기
    @PostMapping("/reservation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RentalBook rentalBook = adminRentalRepository.findById(requestDTO.getRentalBookId())
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));
        ReservationResponseDTO reservation = rentalService.createReservation(rentalBook, userDetails.getMember());

        return ResponseEntity.ok().body(reservation);
    }

}
