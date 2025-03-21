package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesRepository extends JpaRepository<SalesBook, Long> {
    Optional<SalesBook> findById(Long saleId);
    Optional<SalesBook> findByIdAndIsDeletedFalse(Long saleId);

    @Query("SELECT s FROM SalesBook s WHERE (s.title LIKE %:keyword% " +
            "OR s.author LIKE %:keyword% OR s.isbn = :keyword) " +
            "AND s.isDeleted = false AND s.isPublic = true")
    List<SalesBook> searchByKeyword(@Param("keyword") String keyword);
}
