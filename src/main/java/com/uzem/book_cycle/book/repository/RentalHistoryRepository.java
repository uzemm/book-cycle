package com.uzem.book_cycle.book.repository;

import com.uzem.book_cycle.book.entity.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {
}
