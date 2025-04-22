package com.uzem.book_cycle.order.repository;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndShippingStatus(Long orderId, ShippingStatus shippingStatus);
    Optional<Order> findByTossOrderId(String tossOrderId);
    List<Order> findByMemberId(Long memberId);
    boolean existsByMemberAndShippingStatus(Member member,
                                                  ShippingStatus shippingStatus);
}
