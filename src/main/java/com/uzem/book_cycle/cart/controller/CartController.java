package com.uzem.book_cycle.cart.controller;

import com.uzem.book_cycle.cart.dto.CartRequestDTO;
import com.uzem.book_cycle.cart.dto.CartResponseDTO;
import com.uzem.book_cycle.cart.dto.DeleteCartRequestDTO;
import com.uzem.book_cycle.cart.service.CartService;
import com.uzem.book_cycle.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> addCart(@RequestBody @Valid CartRequestDTO requestDTO,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponseDTO cartResponseDTO =
                cartService.addCart(requestDTO, userDetails.getId());
        return ResponseEntity.ok(cartResponseDTO.getCartId());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CartResponseDTO>> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CartResponseDTO> cartList =
                cartService.getCartList(userDetails.getId());
        return ResponseEntity.ok(cartList);
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteCart(@PathVariable Long cartId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.deleteCart(cartId, userDetails.getId());
        return ResponseEntity.ok("장바구니 삭제 성공");
    }
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteCarts(@RequestBody @Valid DeleteCartRequestDTO requestDTO,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.deleteCarts(requestDTO.getCartIds(), userDetails.getId());
        return ResponseEntity.ok("선택한 장바구니 삭제 성공");
    }

}
