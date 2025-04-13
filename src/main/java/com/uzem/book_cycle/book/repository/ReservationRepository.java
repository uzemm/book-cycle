package com.uzem.book_cycle.book.repository;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByRentalBook(RentalBook rentalBook);
    boolean existsByRentalBookAndMember(RentalBook rentalBook, Member member);
    Optional<Reservation> findByRentalBookAndMemberId(RentalBook rentalBook, Long memberId);
    List<Reservation> findAllByMemberId(Long memberId);
    Optional<Reservation> deleteByRentalBook(RentalBook rentalBook);
    List<Reservation> findAllByRentalBook_RentalStatus(RentalStatus rentalStatus);

}
