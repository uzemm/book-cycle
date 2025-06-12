package com.uzem.book_cycle.order.controller;

import com.uzem.book_cycle.order.dto.OrderPaymentRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.service.OrderService;
import com.uzem.book_cycle.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder
            (@RequestBody @Valid OrderPaymentRequestDTO orderPayment,
             @AuthenticationPrincipal CustomUserDetails userDetails){
        LocalDate now = LocalDate.now();
        OrderResponseDTO order = orderService.confirmOrder(
                orderPayment.getOrder(),
                orderPayment.getPayment(),
                userDetails.getId(), now);
        Map<String, String> response = new HashMap<>();
        response.put("orderId", order.getTossOrderId());

        return ResponseEntity.ok().body(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrders(@PathVariable Long orderId){
        OrderResponseDTO orders = orderService.getOrderDetail(orderId);
        return ResponseEntity.ok().body(orders);
    }
}
