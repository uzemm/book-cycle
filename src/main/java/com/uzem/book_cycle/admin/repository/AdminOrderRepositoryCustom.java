package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.dto.order.AdminOrderDetailDTO;
import com.uzem.book_cycle.admin.dto.order.AdminOrderPreviewDTO;
import com.uzem.book_cycle.order.dto.OrderItemResponseDTO;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdminOrderRepositoryCustom {
    Page<AdminOrderPreviewDTO> searchOrders(String memberName,
                                            String email,
                                            String orderNumber,
                                            OrderStatus orderStatus,
                                            ShippingStatus shippingStatus,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            Pageable pageable);

    Optional<AdminOrderDetailDTO> findOrderDetail(Long orderId);
    List<OrderItemResponseDTO> findOrderItems(Long orderId);
}
