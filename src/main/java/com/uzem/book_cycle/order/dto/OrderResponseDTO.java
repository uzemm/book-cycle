package com.uzem.book_cycle.order.dto;

import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private Long shippingFee;
    private Long totalPrice;
    private String receiverName;
    private String receiverZipcode;
    private String receiverAddress;
    private String receiverPhone;
    private Long usedPoint;
    private Long rewardPoint;
    private String orderNumber;
    private List<OrderItemResponseDTO> orderItems;
    private String tossOrderId;
    private OrderStatus status;
    private String orderName;

    public static OrderResponseDTO from(Order order) {
        List<OrderItemResponseDTO> itemResponseDTOS = order.getOrderItems().stream()
                .map(OrderItemResponseDTO::from)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .shippingFee(order.getShippingFee())
                .totalPrice(order.getTotalPrice())
                .receiverName(order.getReceiverName())
                .receiverZipcode(order.getReceiverZipcode())
                .receiverAddress(order.getReceiverAddress())
                .receiverPhone(order.getReceiverPhone())
                .usedPoint(order.getUsedPoint())
                .rewardPoint(order.getRewardPoint())
                .orderNumber(order.getOrderNumber())
                .orderItems(itemResponseDTOS)
                .tossOrderId(order.getTossOrderId())
                .status(order.getOrderStatus())
                .orderName(order.getOrderName())
                .build();
    }
}
