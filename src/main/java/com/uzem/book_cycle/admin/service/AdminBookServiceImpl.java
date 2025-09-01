package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.sales.entity.SalesBook;
import com.uzem.book_cycle.admin.repository.AdminSalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBookServiceImpl {

    private final AdminSalesRepository salesRepository;

    public List<SalesBook> getAllSalesBook() {
        return salesRepository.findAll();
    }
}
