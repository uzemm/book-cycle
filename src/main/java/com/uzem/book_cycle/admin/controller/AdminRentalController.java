package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.rental.AdminRentalRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.UpdateAdminRentalRequestDTO;
import com.uzem.book_cycle.admin.service.AdminRentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    private final AdminRentalService rentalService;

    @PostMapping
    public ResponseEntity<String> createRentalBook(
            @RequestBody @Valid AdminRentalRequestDTO request){
        rentalService.createRentalBook(request);
        return ResponseEntity.ok("대여 도서 등록 완료");
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
}
