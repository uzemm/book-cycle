package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.order.AdminOrderDetailDTO;
import com.uzem.book_cycle.admin.dto.order.AdminOrderPreviewDTO;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminOrderService {
    Page<AdminOrderPreviewDTO> searchOrders(String memberName,
                                            String email,
                                            String orderNumber,
                                            OrderStatus orderStatus,
                                            ShippingStatus shippingStatus,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            Pageable pageable);
    AdminOrderDetailDTO getOrderDetail(Long orderId);
    void updateShippingStatus(Long orderId, String trackingNumber);

}
