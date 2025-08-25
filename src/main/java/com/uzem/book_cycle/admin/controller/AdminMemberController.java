package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.member.*;
import com.uzem.book_cycle.admin.service.AdminMemberService;
import com.uzem.book_cycle.common.ApiResponse;
import com.uzem.book_cycle.common.PageResponse;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
@Tag(name = "관리자 회원 API")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @Operation(summary = "전체 회원 조회 및 검색")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<AdminMemberPreviewDTO>>> searchMembers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MemberStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable)
    {
        Page<AdminMemberPreviewDTO> members = adminMemberService.searchMember(name, email, status, pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(members)));
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminMemberDetailDTO>> getMemberDetail(
            @PathVariable Long memberId)
    {
        return ResponseEntity.ok(ApiResponse.success(
                adminMemberService.getMemberDetail(memberId)));
    }

    @Operation(summary = "회원 상태 수정")
    @PatchMapping("/{memberId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long memberId,
            @RequestBody @Valid AdminMemberStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails admin) {
        adminMemberService.memberStatusUpdate(memberId, request, admin.getUsername());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회원 적립금 수정")
    @PatchMapping("/{memberId}/point")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updatePoint(
            @PathVariable Long memberId,
            @RequestBody @Valid AdminMemberPointUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails admin) {
        adminMemberService.updatePoints(memberId, request, admin.getUsername());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "회원 강제 탈퇴")
    @DeleteMapping("{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails admin
    ){
        adminMemberService.forceDeleteMember(memberId, admin.getUsername());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
