package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.order.AdminOrderDetailDTO;
import com.uzem.book_cycle.admin.dto.order.AdminOrderPreviewDTO;
import com.uzem.book_cycle.admin.service.AdminOrderService;
import com.uzem.book_cycle.common.ApiResponse;
import com.uzem.book_cycle.common.PageResponse;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@Tag(name = "관리자 주문 API")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "전체 주문 조회 및 검색")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AdminOrderPreviewDTO>>> searchOrders(
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) ShippingStatus shippingStatus,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
            Pageable pageable)
    {
        Page<AdminOrderPreviewDTO> orders  = adminOrderService.searchOrders(
                memberName, email, orderNumber, orderStatus,
                shippingStatus, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(orders)));
    }

    @Operation(summary = "주문 상세 조회")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminOrderDetailDTO>> getOrderDetail(@PathVariable Long orderId) {
        AdminOrderDetailDTO detail = adminOrderService.getOrderDetail(orderId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @Operation(summary = "운송장 입력 및 배송 상태 업데이트")
    @PostMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateTrackingNumber(@PathVariable Long orderId,
                                                       @RequestParam String trackingNumber) {
        adminOrderService.updateShippingStatus(orderId, trackingNumber);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
