package com.uzem.book_cycle.wish.controller;

import com.uzem.book_cycle.security.CustomUserDetails;
import com.uzem.book_cycle.wish.dto.WishResponseDTO;
import com.uzem.book_cycle.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {
    private final WishService wishService;

    @PostMapping("/{salesBookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WishResponseDTO> addWish(@PathVariable Long salesBookId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        WishResponseDTO wishResponseDTO =
                wishService.addWishBook(salesBookId, userDetails.getId());
        return ResponseEntity.ok(wishResponseDTO);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WishResponseDTO>> getWishBookList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<WishResponseDTO> wishBookList =
                wishService.getWishBookList(userDetails.getId());
        return ResponseEntity.ok(wishBookList);
    }

    @DeleteMapping("/{salesBookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteWishBook(
            @PathVariable Long salesBookId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        wishService.deleteWishBook(salesBookId, userDetails.getId());
        return ResponseEntity.ok("관심도서 삭제 완료");
    }

}
