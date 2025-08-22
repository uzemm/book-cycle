package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.member.AdminMemberDetailDTO;
import com.uzem.book_cycle.admin.dto.member.AdminMemberPointUpdateRequest;
import com.uzem.book_cycle.admin.dto.member.AdminMemberPreviewDTO;
import com.uzem.book_cycle.admin.dto.member.AdminMemberStatusUpdateRequest;
import com.uzem.book_cycle.member.type.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminMemberService {
    void memberStatusUpdate(
            Long memberId, AdminMemberStatusUpdateRequest request, String adminName);

    void updatePoints(
            Long memberId, AdminMemberPointUpdateRequest request, String adminName);
    Page<AdminMemberPreviewDTO> searchMember(
            String name, String email, MemberStatus status, Pageable pageable);
    Optional<AdminMemberDetailDTO> getMemberDetail(Long memberId);
    void forceDeleteMember(Long memberId, String adminName);
}
