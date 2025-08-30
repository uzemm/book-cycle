package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.order.AdminOrderDetailDTO;
import com.uzem.book_cycle.admin.dto.order.AdminOrderPreviewDTO;
import com.uzem.book_cycle.admin.repository.AdminOrderRepository;
import com.uzem.book_cycle.event.OrderShippedEvent;
import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.exception.PaymentException;
import com.uzem.book_cycle.order.dto.OrderItemResponseDTO;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import com.uzem.book_cycle.external.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.payment.entity.TossPayment;
import com.uzem.book_cycle.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.uzem.book_cycle.notification.type.NotificationType.ORDER_SHIPPED;
import static com.uzem.book_cycle.order.type.OrderErrorCode.ORDER_NOT_FOUND;
import static com.uzem.book_cycle.external.payment.type.PaymentErrorCode.PAYMENT_FAILED;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService{

    private final AdminOrderRepository adminOrderRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<AdminOrderPreviewDTO>  searchOrders(String memberName,
                                                    String email,
                                                    String orderNumber,
                                                    OrderStatus orderStatus,
                                                    ShippingStatus shippingStatus,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    Pageable pageable){
        return adminOrderRepository.searchOrders(
                memberName, email, orderNumber, orderStatus, shippingStatus,
                startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailDTO getOrderDetail(Long orderId){
        AdminOrderDetailDTO orderDetail = adminOrderRepository.findOrderDetail(orderId)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        List<OrderItemResponseDTO> items = adminOrderRepository.findOrderItems(orderId);
        TossPayment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException(PAYMENT_FAILED));

        return AdminOrderDetailDTO.builder()
                .id(orderDetail.getId())
                .orderNumber(orderDetail.getOrderNumber())
                .memberName(orderDetail.getMemberName())
                .memberEmail(orderDetail.getMemberEmail())
                .memberPhone(orderDetail.getMemberPhone())
                .memberAddress(orderDetail.getMemberAddress())
                .orderStatus(orderDetail.getOrderStatus())
                .shippingStatus(orderDetail.getShippingStatus())
                .createdAt(orderDetail.getCreatedAt())
                .totalPrice(orderDetail.getTotalPrice())
                .items(items)
                .payment(payment != null ? PaymentResponseDTO.from(payment) : null)
                .build();
    }

    @Transactional
    public void updateShippingStatus(Long orderId, String trackingNumber){
        Order order = adminOrderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        if (order.getShippingStatus() == ShippingStatus.PREPARING) {
            order.shipOrder(trackingNumber);
        } else {
            order.updateTrackingNumber(trackingNumber);
        }

        // 알림 발송
        String message = ORDER_SHIPPED.format(trackingNumber
                , 0);

        eventPublisher.publishEvent(
                new OrderShippedEvent(order.getId(), message));
    }
}
