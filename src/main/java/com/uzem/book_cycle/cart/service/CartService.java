package com.uzem.book_cycle.cart.service;

import com.uzem.book_cycle.cart.dto.CartRequestDTO;
import com.uzem.book_cycle.cart.dto.CartResponseDTO;

import java.util.List;

public interface CartService {
    CartResponseDTO addCart(CartRequestDTO request, Long memberId);
    List<CartResponseDTO> getCartList(Long memberId);
    void deleteCart(Long cartId, Long memberId);
    void deleteCarts(List<Long> cartIds, Long memberId);
}
