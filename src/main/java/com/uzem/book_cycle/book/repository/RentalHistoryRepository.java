package com.uzem.book_cycle.book.repository;

import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {

    List<RentalHistory> findAllByRentalStatus(RentalStatus rentalStatus);
    List<RentalHistory> findAllByRentalStatusAndReturnDateBefore(RentalStatus rentalStatus, LocalDate today);
    List<RentalHistory> findAllByRentalStatusAndMemberIdOrderByReturnDateAsc(
            RentalStatus rentalStatus, Long memberId);
    List<RentalHistory> findAllByOrderId(Long orderId);
    List<RentalHistory> findAllByReturnDate(LocalDate minus);
    boolean existsByRentalStatusAndOrderId(RentalStatus rentalStatus, Long orderId);
}
