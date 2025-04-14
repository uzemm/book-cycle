package com.uzem.book_cycle.cart.repository;

import com.uzem.book_cycle.cart.entity.Cart;
import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByMember(Member member);
    Optional<Cart> findByIdAndMemberId(Long cartId, Long memberId);
    List<Cart> findAllByIdInAndMemberId(List<Long> cartIds, Long memberId);
    List<Cart> findAllByIdInAndMember(List<Long> cartIds, Member member);
}
