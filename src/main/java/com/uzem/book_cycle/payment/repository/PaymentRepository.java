package com.uzem.book_cycle.payment.repository;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.payment.entity.TossPayment;
import com.uzem.book_cycle.payment.type.PaymentPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<TossPayment, Long> {

    Optional<TossPayment> findByOrderId(Long orderId);
    Optional<TossPayment> findByPaymentKey(String paymentKey);
    Optional<TossPayment> findByOrderAndPaymentPurpose(Order order, PaymentPurpose paymentPurpose);
}
