package com.uzem.book_cycle.order.controller;

import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder
            (@RequestBody @Valid OrderRequestDTO request){
        OrderResponseDTO order = orderService.createOrder(request);

        return ResponseEntity.ok().body(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrders(@PathVariable Long orderId){
        OrderResponseDTO orders = orderService.getOrders(orderId);
        return ResponseEntity.ok().body(orders);
    }
}
