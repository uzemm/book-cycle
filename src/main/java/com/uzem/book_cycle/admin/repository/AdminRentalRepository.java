package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.entity.RentalBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRentalRepository extends JpaRepository<RentalBook, Long> {

    Optional<RentalBook> findById(Long rentalId);
    Optional<RentalBook> findByIdAndIsDeletedFalse(Long rentalId);

    @Query("SELECT r FROM RentalBook r WHERE (r.title LIKE %:keyword% " +
            "OR r.author LIKE %:keyword% OR r.isbn = :keyword)"
            + "AND r.isDeleted = false AND r.isPublic = true")
    List<RentalBook> searchByKeyword(String keyword);
}
