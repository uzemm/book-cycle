package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.sales.AdminSalesRequestDTO;
import com.uzem.book_cycle.admin.dto.sales.AdminSalesResponseDTO;
import com.uzem.book_cycle.admin.dto.sales.UpdateAdminSalesRequestDTO;
import com.uzem.book_cycle.admin.entity.SalesBook;

import java.util.List;

public interface AdminSalesService {
    AdminSalesResponseDTO createSalesBook(AdminSalesRequestDTO salesRequestDTO);
    AdminSalesResponseDTO getSalesBookDetail(Long saleId);
    void updateSalesBook(Long saleId, UpdateAdminSalesRequestDTO update);
    void deleteSalesBook(Long saleId);
    List<SalesBook> searchSalesBook(String keyword);
}