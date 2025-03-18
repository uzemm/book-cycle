package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import com.uzem.book_cycle.admin.dto.sales.SalesRequestDTO;
import com.uzem.book_cycle.admin.dto.sales.SalesResponseDTO;
import com.uzem.book_cycle.admin.dto.sales.UpdateSalesRequestDTO;
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
    public ResponseEntity<SalesBook> salesRegister(
            @RequestBody @Valid SalesRequestDTO request){
        SalesBook salesBook = salesService.registerSales(request);
        return ResponseEntity.ok(salesBook);

    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SalesResponseDTO> salesDetail(@PathVariable Long saleId){
        SalesResponseDTO response = salesService.getSalesBookDetail(saleId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{saleId}")
    public ResponseEntity<String> updateSalesBook(
            @PathVariable Long saleId,@RequestBody @Valid UpdateSalesRequestDTO request){
        salesService.updateSalesBook(saleId, request);
        return ResponseEntity.ok("판매 도서 수정 완료");
    }

    @DeleteMapping("/{saleId}")
    public ResponseEntity<String> deleteSalesBook(@PathVariable Long saleId){
        salesService.deleteSalesBook(saleId);
        return ResponseEntity.ok("판매 도서 삭제 완료");
    }

}
