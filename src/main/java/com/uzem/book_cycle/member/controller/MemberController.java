package com.uzem.book_cycle.member.controller;

import com.uzem.book_cycle.member.dto.MemberResponseDTO;
import com.uzem.book_cycle.member.dto.MemberUpdatePasswordRequestDTO;
import com.uzem.book_cycle.member.dto.MemberUpdateRequestDTO;
import com.uzem.book_cycle.member.service.MemberService;
import com.uzem.book_cycle.security.CustomUserDetails;
import com.uzem.book_cycle.security.token.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(memberService.getMyInfo(userDetails.getId()));
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid MemberUpdateRequestDTO request) {

        Long memberId = userDetails.getId();
        MemberResponseDTO updateMyInfo =
                memberService.updateMyInfo(memberId, request);

        return ResponseEntity.ok(updateMyInfo);
    }

    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyPassword(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody@Valid MemberUpdatePasswordRequestDTO requestDTO){

        Long memberId = userDetails.getId();
        String accessToken = tokenProvider.resolveToken(request);

        memberService.updatePassword(memberId, requestDTO, accessToken);

        return ResponseEntity.ok("비밀번호 변경 성공");
    }
}
