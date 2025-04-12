package com.uzem.book_cycle.wish.repository;

import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByMemberAndSalesBook(Member member, SalesBook salesBook);
    List<Wish> findByMember(Member member);
    Optional<Wish> findByMemberAndSalesBookId(Member member, Long salesBookId);
}
