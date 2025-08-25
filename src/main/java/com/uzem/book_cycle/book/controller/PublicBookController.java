package com.uzem.book_cycle.book.controller;

import com.uzem.book_cycle.book.entity.RentalBook;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalResponseDTO;
import com.uzem.book_cycle.book.entity.SalesBook;
import com.uzem.book_cycle.admin.dto.sales.AdminSalesResponseDTO;
import com.uzem.book_cycle.admin.service.AdminRentalServiceImpl;
import com.uzem.book_cycle.admin.service.AdminSalesServiceImpl;
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

    private final AdminSalesServiceImpl salesService;
    private final AdminRentalServiceImpl rentalService;

    @GetMapping("/sales/{saleId}")
    public ResponseEntity<AdminSalesResponseDTO> salesDetail(@PathVariable Long saleId){
        AdminSalesResponseDTO response = salesService.getSalesBookDetail(saleId);
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
