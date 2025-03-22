package com.uzem.book_cycle.book.controller;

import com.uzem.book_cycle.admin.dto.rentals.RentalsBook;
import com.uzem.book_cycle.admin.dto.rentals.RentalsResponseDTO;
import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import com.uzem.book_cycle.admin.dto.sales.SalesResponseDTO;
import com.uzem.book_cycle.admin.service.RentalsService;
import com.uzem.book_cycle.admin.service.SalesService;
import com.uzem.book_cycle.book.dto.RentalsPreviewDTO;
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
    private final RentalsService rentalsService;

    @GetMapping("/sales/{saleId}")
    public ResponseEntity<SalesResponseDTO> salesDetail(@PathVariable Long saleId){
        SalesResponseDTO response = salesService.getSalesBookDetail(saleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rentals/{rentalId}")
    public ResponseEntity<RentalsResponseDTO> rentalsDetail(@PathVariable Long rentalId){
        RentalsResponseDTO response = rentalsService.getRentalsBookDetail(rentalId);
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
                .map(SalesBook::toPreviewDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/rentals")
    @ResponseBody
    public List<RentalsPreviewDTO> searchRentals(@RequestParam("keyword") String keyword) {
        if(!StringUtils.hasText(keyword)) {
            throw new BookException(EMPTY_SEARCH_QUERY);
        }
        return rentalsService.searchRentalsBook(keyword).stream()
                .map(RentalsBook::toSalesPreviewDTO)
                .collect(Collectors.toList());
    }
}
