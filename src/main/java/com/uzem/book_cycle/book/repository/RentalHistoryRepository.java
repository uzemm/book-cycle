package com.uzem.book_cycle.book.repository;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {

    List<RentalHistory> findAllByRentalStatus(RentalStatus rentalStatus);
    List<RentalHistory> findAllByRentalStatusAndMember(RentalStatus rentalStatus, Member member);
    Optional<RentalHistory> findByRentalBookAndMember(RentalBook rentalBook, Member member);
}
