package com.uzem.book_cycle.order.entity;

import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.type.*;
import com.uzem.book_cycle.external.payment.type.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.uzem.book_cycle.order.type.OrderErrorCode.INVALID_ORDER_STATUS;
import static com.uzem.book_cycle.order.type.OrderStatus.*;
import static com.uzem.book_cycle.order.type.ShippingStatus.SHIPPED;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Long shippingFee;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverZipcode;

    @Column(nullable = false)
    private String receiverAddress;

    @Column(nullable = false)
    private String receiverPhone;

    private String deliveryMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus shippingStatus;

    @Column(nullable = false)
    private Long usedPoint;

    @Column(nullable = false)
    private Long rewardPoint;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,  orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    private String tossOrderId;

    @Setter
    private String orderName;

    @Enumerated(EnumType.STRING)
    private CancelReason cancelReason;

    private String trackingNumber;

    public void addOrderItem(OrderItem orderItem) {
        if(this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    @PrePersist
    public void generateIds(){
        if(this.orderNumber == null) {
            this.orderNumber = OrderNumberGenerator.createOrderNumber();
        }
        if(this.tossOrderId == null){
            this.tossOrderId = "TOSS" + this.orderNumber;
        }
    }

    public static Order from(OrderRequestDTO request,
                             List<OrderItem> orderItems, Member member) {

        return Order.builder()
                .member(member)
                .receiverZipcode(request.getReceiverZipcode())
                .receiverAddress(request.getReceiverAddress())
                .receiverPhone(request.getReceiverPhone())
                .receiverName(request.getReceiverName())
                .deliveryMessage(request.getDeliveryMessage())
                .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint() : 0)
                .orderStatus(PAID_READY)
                .paymentMethod(request.getPaymentMethod())
                .rewardPoint(100L)
                .shippingFee(3500L)
                .shippingStatus(SHIPPED)
                .build();
    }

    // 사용한 포인트
    public Long getUsedPoint() {
        return this.usedPoint != null ? this.usedPoint : 0L;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setRewardPoint(long rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public void orderStatusCompleted() {
        this.orderStatus = COMPLETED;
    }

    public void cancelRequestOrder() {
        this.orderStatus = CANCEL_REQUESTED;
    }

    public void cancelOrder(CancelReason cancelReason) {
        this.orderStatus = CANCELED;
        this.cancelReason = cancelReason;
    }

    public void cancelPending(){
        this.orderStatus = CANCEL_PENDING;
    }

    // 배송 시작 (READY → SHIPPED)
    public void shipOrder(String trackingNumber) {
        if(this.shippingStatus != ShippingStatus.PREPARING) {
            throw new OrderException(INVALID_ORDER_STATUS);
        }
        this.trackingNumber = trackingNumber;
        this.shippingStatus = SHIPPED;
    }

    // 운송장 번호 수정 (SHIPPED까지만 허용)
    public void updateTrackingNumber(String trackingNumber) {
        if (this.shippingStatus == ShippingStatus.DELIVERED) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.trackingNumber = trackingNumber;
    }
}
