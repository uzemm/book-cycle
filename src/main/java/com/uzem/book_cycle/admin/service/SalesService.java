package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import com.uzem.book_cycle.admin.dto.sales.SalesRequestDTO;
import com.uzem.book_cycle.admin.dto.sales.SalesResponseDTO;
import com.uzem.book_cycle.admin.dto.sales.UpdateSalesRequestDTO;
import com.uzem.book_cycle.admin.repository.SalesRepository;
import com.uzem.book_cycle.exception.SalesException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.uzem.book_cycle.admin.type.SalesErrorCode.SALES_BOOK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesBookRepository;

    public SalesBook createSalesBook(SalesRequestDTO salesRequestDTO) {
        SalesBook book = salesRequestDTO.toSalesBook();
        return salesBookRepository.save(book);
    }

    public SalesResponseDTO getSalesBookDetail(Long saleId) {
        SalesBook salesBook = salesBookRepository.findById(saleId).orElseThrow(
                () -> new SalesException(SALES_BOOK_NOT_FOUND));

        return SalesResponseDTO.create(salesBook);
    }

    @Transactional
    public void updateSalesBook(Long saleId, UpdateSalesRequestDTO update) {
        SalesBook salesBook = salesBookRepository.findById(saleId).orElseThrow(
                () -> new SalesException(SALES_BOOK_NOT_FOUND));

        salesBook.updateSalesBook(update);
    }

    @Transactional
    public void deleteSalesBook(Long saleId) {
        SalesBook salesBook = salesBookRepository.findByIdAndIsDeletedFalse(saleId)
                .orElseThrow(() -> new SalesException(SALES_BOOK_NOT_FOUND));

        salesBook.delete();
    }
}
