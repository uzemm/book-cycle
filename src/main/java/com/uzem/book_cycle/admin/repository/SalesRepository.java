package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalesRepository extends JpaRepository<SalesBook, Long> {
    Optional<SalesBook> findById(Long saleId);
    Optional<SalesBook> findByIdAndIsDeletedFalse(Long saleId);
}
