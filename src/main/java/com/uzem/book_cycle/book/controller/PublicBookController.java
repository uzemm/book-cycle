package com.uzem.book_cycle.book.controller;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalResponseDTO;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.dto.sales.SalesResponseDTO;
import com.uzem.book_cycle.admin.service.AdminRentalService;
import com.uzem.book_cycle.admin.service.SalesService;
import com.uzem.book_cycle.book.dto.RentalPreviewDTO;
import com.uzem.book_cycle.book.dto.SalesPreviewDTO;
import com.uzem.book_cycle.exception.BookException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.naver.type.BookErrorCode.EMPTY_SEARCH_QUERY;

@Controller
@RequiredArgsConstructor
public class PublicBookController {

    private final SalesService salesService;
    private final AdminRentalService rentalService;

    @GetMapping("/sales/{saleId}")
    public ResponseEntity<SalesResponseDTO> salesDetail(@PathVariable Long saleId){
        SalesResponseDTO response = salesService.getSalesBookDetail(saleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rentals/{rentalId}")
    public ResponseEntity<AdminRentalResponseDTO> rentalDetail(@PathVariable Long rentalId){
        AdminRentalResponseDTO response = rentalService.getRentalBookDetail(rentalId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sales")
    @ResponseBody
    public List<SalesPreviewDTO> searchSalesBooks(@RequestParam("keyword") String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new BookException(EMPTY_SEARCH_QUERY);
        }
        return salesService.searchSalesBook(keyword)
                .stream()
                .map(SalesBook::toSalesPreviewDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/rentals")
    @ResponseBody
    public List<RentalPreviewDTO> searchRentalBooks(@RequestParam("keyword") String keyword) {
        if(!StringUtils.hasText(keyword)) {
            throw new BookException(EMPTY_SEARCH_QUERY);
        }
        return rentalService.searchRentalBook(keyword).stream()
                .map(RentalBook::toRentalPreviewDTO)
                .collect(Collectors.toList());
    }
}
