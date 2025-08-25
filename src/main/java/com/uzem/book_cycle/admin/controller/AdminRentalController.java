package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.rental.AdminRentalRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalStatusDTO;
import com.uzem.book_cycle.admin.dto.rental.UpdateAdminRentalRequestDTO;
import com.uzem.book_cycle.book.entity.RentalBook;
import com.uzem.book_cycle.admin.service.AdminRentalService;
import com.uzem.book_cycle.admin.type.RentalStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    private final AdminRentalService rentalService;

    @PostMapping
    public ResponseEntity<Long> createRentalBook(
            @RequestBody @Valid AdminRentalRequestDTO request){
        RentalBook rentalBook = rentalService.createRentalBook(request);
        return ResponseEntity.ok(rentalBook.getId());
    }

    @PatchMapping("/{rentalId}")
    public ResponseEntity<String> updateRentalBook(
            @PathVariable Long rentalId, @RequestBody @Valid UpdateAdminRentalRequestDTO request){
        rentalService.updateRentalBook(rentalId, request);
        return ResponseEntity.ok("대여 도서 수정 완료");
    }

    @DeleteMapping("/{rentalId}")
    public ResponseEntity<String> deleteRentalBook(@PathVariable Long rentalId){
        rentalService.deleteRentalBook(rentalId);
        return ResponseEntity.ok("대여 도서 삭제 완료");
    }

    @GetMapping
    public ResponseEntity<Page<AdminRentalStatusDTO>> searchRentals(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) RentalStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
            Pageable pageable
            )
    {
        return ResponseEntity.ok(rentalService.searchRentals(
                memberId, status, startDate, endDate, pageable));
    }

}
