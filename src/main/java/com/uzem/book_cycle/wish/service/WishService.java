package com.uzem.book_cycle.wish.service;

import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.wish.dto.WishResponseDTO;

import java.util.List;

public interface WishService {

    WishResponseDTO addWishBook(Long salesId, Member member);
    List<WishResponseDTO> getWishBookList(Member member);
    void deleteWishBook(Long salesId, Member member);
}
