package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.dto.rentals.RentalsBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalsRepository extends JpaRepository<RentalsBook, Long> {

    Optional<RentalsBook> findById(Long rentalId);
    Optional<RentalsBook> findByIdAndIsDeletedFalse(Long rentalId);
}
