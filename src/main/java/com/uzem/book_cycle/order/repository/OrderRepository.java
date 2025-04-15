package com.uzem.book_cycle.order.repository;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.ShippingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(Long orderId);
    Optional<Order> findByTossOrderId(String tossOrderId);
    boolean existsByMemberAndShippingStatus(Member member,
                                                  ShippingStatus shippingStatus);
}
