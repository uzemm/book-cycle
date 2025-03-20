package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.dto.rentals.RentalsBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalsRepository extends JpaRepository<RentalsBook, Long> {

    Optional<RentalsBook> findById(Long rentalId);
    Optional<RentalsBook> findByIdAndIsDeletedFalse(Long rentalId);

    @Query("SELECT r FROM RentalsBook r WHERE (r.title LIKE %:keyword% " +
            "OR r.author LIKE %:keyword% OR r.isbn = :keyword)"
            + "AND r.isDeleted = false AND r.isPublic = true")
    List<RentalsBook> searchByKeyword(String keyword);
}
