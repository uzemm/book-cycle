package com.uzem.book_cycle.wish.service;

import com.uzem.book_cycle.wish.dto.WishResponseDTO;

import java.util.List;

public interface WishService {

    WishResponseDTO addWishBook(Long salesId, Long memberId);
    List<WishResponseDTO> getWishBookList(Long memberId);
    void deleteWishBook(Long salesId, Long memberId);
}
