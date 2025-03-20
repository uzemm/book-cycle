package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.rentals.RentalsRequestDTO;
import com.uzem.book_cycle.admin.dto.rentals.UpdateRentalsRequestDTO;
import com.uzem.book_cycle.admin.service.RentalsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rentals")
public class RentalsController {

    private final RentalsService rentalsService;

    @PostMapping
    public ResponseEntity<String> createRentalsBook(
            @RequestBody @Valid RentalsRequestDTO request){
        rentalsService.createRentalsBook(request);
        return ResponseEntity.ok("대여 도서 등록 완료");
    }

    @PatchMapping("/{rentalId}")
    public ResponseEntity<String> updateRentalsBook(
            @PathVariable Long rentalId, @RequestBody @Valid UpdateRentalsRequestDTO request){
        rentalsService.updateRentalsBook(rentalId, request);
        return ResponseEntity.ok("대여 도서 수정 완료");
    }

    @DeleteMapping("/{rentalId}")
    public ResponseEntity<String> deleteRentalsBook(@PathVariable Long rentalId){
        rentalsService.deleteRentalsBook(rentalId);
        return ResponseEntity.ok("대여 도서 삭제 완료");
    }
}
