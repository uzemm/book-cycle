package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.dto.member.AdminMemberDetailDTO;
import com.uzem.book_cycle.admin.dto.member.AdminMemberPreviewDTO;
import com.uzem.book_cycle.member.type.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AdminMemberRepositoryCustom {

    Page<AdminMemberPreviewDTO> searchMember(
            String name, String email, MemberStatus status, Pageable pageable);
    Optional<AdminMemberDetailDTO> getMemberDetail(Long memberId);
}
