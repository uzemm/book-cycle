package com.uzem.book_cycle.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cancel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String cancelReason;

    private Long cancelAmount;

    private OffsetDateTime canceledAt;

    @Column(nullable = false)
    private String transactionKey;

    @Column(nullable = false)
    private String cancelStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private TossPayment payment;

    private Long refundableAmount;

    public void setPayment(TossPayment payment) {
        this.payment = payment;
    }
}
