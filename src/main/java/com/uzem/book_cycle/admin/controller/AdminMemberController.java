package com.uzem.book_cycle.admin.controller;

import com.uzem.book_cycle.admin.dto.member.*;
import com.uzem.book_cycle.admin.service.AdminMemberService;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<Page<AdminMemberPreviewDTO>> searchMembers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MemberStatus status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(adminMemberService.searchMember(name, email, status, pageable));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<AdminMemberDetailDTO> getMemberDetail(@PathVariable Long memberId){
        return adminMemberService.getMemberDetail(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<AdminResponse> updateStatus(
            @PathVariable Long memberId,
            @RequestBody @Valid AdminMemberStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails admin) {
        adminMemberService.memberStatusUpdate(memberId, request, admin.getUsername());

        return ResponseEntity.ok(AdminResponse.of("회원 상태가 " + request.getStatus() + "로 변경되었습니다."));
    }

    @PatchMapping("/{memberId}/point")
    public ResponseEntity<AdminResponse> updatePoint(
            @PathVariable Long memberId,
            @RequestBody @Valid AdminMemberPointUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails admin) {
        adminMemberService.updatePoints(memberId, request, admin.getUsername());

        return ResponseEntity.ok(AdminResponse.of("회원 포인트가 " + request.getAmount() + "로 변경되었습니다."));
    }

    @DeleteMapping("{memberId}")
    public ResponseEntity<AdminResponse> deleteMember(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails admin
    ){
        adminMemberService.forceDeleteMember(memberId, admin.getUsername());

        return ResponseEntity.ok(AdminResponse.of("회원을 강제 탈퇴 하였습니다."));
    }

}
