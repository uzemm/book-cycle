package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.sales.AdminSalesRequestDTO;
import com.uzem.book_cycle.admin.dto.sales.AdminSalesResponseDTO;
import com.uzem.book_cycle.admin.dto.sales.UpdateAdminSalesRequestDTO;
import com.uzem.book_cycle.admin.service.AdminSalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/sales")
public class AdminSalesController {

    private final AdminSalesService salesService;

    @PostMapping
    public ResponseEntity<AdminSalesResponseDTO> createSalesBook(
            @RequestBody @Valid AdminSalesRequestDTO request){
        AdminSalesResponseDTO salesBook = salesService.createSalesBook(request);
        return ResponseEntity.ok(salesBook);
    }

    @PatchMapping("/{salesId}")
    public ResponseEntity<String> updateSalesBook(
            @PathVariable Long salesId,@RequestBody @Valid UpdateAdminSalesRequestDTO request){
        salesService.updateSalesBook(salesId, request);
        return ResponseEntity.ok("판매 도서 수정 완료");
    }

    @DeleteMapping("/{salesId}")
    public ResponseEntity<String> deleteSalesBook(@PathVariable Long salesId){
        salesService.deleteSalesBook(salesId);
        return ResponseEntity.ok("판매 도서 삭제 완료");
    }

}
