package com.uzem.book_cycle.book.repository;

import com.uzem.book_cycle.book.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByRentalBookAndMemberAndIsActiveTrue(RentalBook rentalBook, Member member);
    Optional<Reservation> findByRentalBookAndMemberIdAndIsActiveTrue(RentalBook rentalBook, Long memberId);
    List<Reservation> findAllByMemberIdAndIsActiveTrue(Long memberId);
    Optional<Reservation> deleteByRentalBook(RentalBook rentalBook);
    List<Reservation> findAllByRentalBook_RentalStatusAndIsActiveTrueAndPaymentDeadlineBefore(RentalStatus rentalStatus, LocalDate paymentDeadline);
    boolean existsByMemberId(Long memberId);
    Optional<Reservation> findFirstByRentalBookAndRentalBook_RentalStatusAndIsActiveTrueOrderByReservationOrderAsc(
            RentalBook rentalBook, RentalStatus rentalStatus);
    boolean existsByMemberAndActiveTrueAndPaymentDeadlineAfter(Member member, LocalDate now);
}
