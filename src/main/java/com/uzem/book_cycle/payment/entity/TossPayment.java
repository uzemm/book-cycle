package com.uzem.book_cycle.payment.entity;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.type.PaymentStatus;
import com.uzem.book_cycle.payment.type.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
public class TossPayment{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentKey;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Column(nullable = false)
    private String tossOrderId; // 토스에서 관리하는 id

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private OffsetDateTime requestedAt;

    private OffsetDateTime approvedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Cancel> cancels = new ArrayList<>();

    public static TossPayment from(PaymentRequestDTO request, Order order) {
        return TossPayment.builder()
                .paymentKey(request.getPaymentKey())
                .tossOrderId(request.getTossOrderId())
                .type(request.getType())
                .order(order)
                .amount(request.getAmount())
                .status(request.getStatus())
                .orderName("BookCycle 책 1권")
                .requestedAt(OffsetDateTime.now())
                .approvedAt(OffsetDateTime.now())
                .build();
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void addCancel(Cancel cancel) {
        this.cancels.add(cancel);
        cancel.setPayment(this);
    }
}
