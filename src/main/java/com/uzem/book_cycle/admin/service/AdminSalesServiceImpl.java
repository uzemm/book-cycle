package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.book.entity.SalesBook;
import com.uzem.book_cycle.admin.dto.sales.AdminSalesRequestDTO;
import com.uzem.book_cycle.admin.dto.sales.AdminSalesResponseDTO;
import com.uzem.book_cycle.admin.dto.sales.UpdateAdminSalesRequestDTO;
import com.uzem.book_cycle.admin.repository.AdminSalesRepository;
import com.uzem.book_cycle.exception.SalesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.uzem.book_cycle.admin.type.SalesErrorCode.SALES_BOOK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminSalesServiceImpl implements AdminSalesService {

    private final AdminSalesRepository salesBookRepository;

    @Transactional
    public AdminSalesResponseDTO createSalesBook(AdminSalesRequestDTO request) {
        SalesBook book = SalesBook.from(request);
        SalesBook saved = salesBookRepository.save(book);
        return AdminSalesResponseDTO.create(saved);
    }

    @Transactional(readOnly = true)
    public AdminSalesResponseDTO getSalesBookDetail(Long saleId) {
        SalesBook salesBook = salesBookRepository.findById(saleId).orElseThrow(
                () -> new SalesException(SALES_BOOK_NOT_FOUND));
        return AdminSalesResponseDTO.create(salesBook);
    }

    @Transactional
    public void updateSalesBook(Long saleId, UpdateAdminSalesRequestDTO update) {
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

    @Transactional
    public List<SalesBook> searchSalesBook(String keyword) {
        return salesBookRepository.searchByKeyword(keyword);
    }
}
