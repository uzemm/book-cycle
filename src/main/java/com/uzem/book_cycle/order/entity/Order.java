package com.uzem.book_cycle.order.entity;

import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.type.ItemType;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.payment.type.PaymentMethod;
import com.uzem.book_cycle.order.type.ShippingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.uzem.book_cycle.order.type.OrderStatus.PAID_READY;

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

    private String orderName;

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order from(OrderRequestDTO request,
                             List<OrderItem> orderItems, Member member) {
        Order order = Order.builder()
                .member(member)
                .receiverZipcode(request.getReceiverZipcode())
                .receiverAddress(request.getReceiverAddress())
                .receiverPhone(request.getReceiverPhone())
                .receiverName(request.getReceiverName())
                .deliveryMessage(request.getDeliveryMessage())
                .paymentMethod(request.getPaymentMethod())
                .usedPoint(request.getUsedPoint() != null ? request.getUsedPoint() : 0)
                .orderNumber(createOrderNumber())
                .orderStatus(PAID_READY)
                .paymentMethod(request.getPaymentMethod())
                .rewardPoint(100L)
                .shippingFee(3500L)
                .shippingStatus(ShippingStatus.SHIPPED)
                .tossOrderId(generateTossOrderId())
                .orderName(createOrderName(orderItems))
                .build();

        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
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

    public static String createOrderNumber() {
        return "BC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + RandomUtils.nextInt(100, 999);
    }

    private static String generateTossOrderId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private static String createOrderName(List<OrderItem> orderItems){
        int count = orderItems.size();
        String title;
        if(orderItems.get(0).getItemType() == ItemType.SALE ){
            title = orderItems.get(0).getSalesBook().getTitle();
        } else {
            title = orderItems.get(0).getRentalBook().getTitle();
        }

        return count == 1 ? title : title + " 외 " + (count-1) + "권";
    }
}
