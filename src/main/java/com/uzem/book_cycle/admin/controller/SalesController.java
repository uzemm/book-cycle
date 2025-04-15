package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.sales.SalesRequestDTO;
import com.uzem.book_cycle.admin.dto.sales.UpdateSalesRequestDTO;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/sales")
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    public ResponseEntity<Long> createSalesBook(
            @RequestBody @Valid SalesRequestDTO request){
        SalesBook salesBook = salesService.createSalesBook(request);
        return ResponseEntity.ok(salesBook.getId());
    }

    @PatchMapping("/{salesId}")
    public ResponseEntity<String> updateSalesBook(
            @PathVariable Long salesId,@RequestBody @Valid UpdateSalesRequestDTO request){
        salesService.updateSalesBook(salesId, request);
        return ResponseEntity.ok("판매 도서 수정 완료");
    }

    @DeleteMapping("/{salesId}")
    public ResponseEntity<String> deleteSalesBook(@PathVariable Long salesId){
        salesService.deleteSalesBook(salesId);
        return ResponseEntity.ok("판매 도서 삭제 완료");
    }

}
