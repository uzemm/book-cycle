package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.member.*;
import com.uzem.book_cycle.admin.entity.AdminLog;
import com.uzem.book_cycle.admin.repository.AdminLogRepository;
import com.uzem.book_cycle.admin.repository.AdminMemberRepository;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static com.uzem.book_cycle.admin.type.LogActionType.*;
import static com.uzem.book_cycle.member.type.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService{

    private final MemberRepository memberRepository;
    private final AdminLogRepository adminLogRepository;
    private final AdminMemberRepository adminMemberRepository;
    private final RedisUtil redisUtil;

    // 멤버 상태 변경
    @Transactional
    public void memberStatusUpdate(
            Long memberId, AdminMemberStatusUpdateRequest request, String adminName){

        Member member = getMember(memberId);

        MemberStatus preStatus = member.getStatus();
        member.changeStatus(request.getStatus());

        // 로그 남기기
        String reason = String.format("회원 상태 변경 : %s -> %s", preStatus, member.getStatus());
        adminLogRepository.save(AdminLog.of(member, STATUS_CHANGE, reason, adminName));
    }

    @Transactional
    public void updatePoints(
            Long memberId, AdminMemberPointUpdateRequest request, String adminName){

        Member member = getMember(memberId);

        member.changePoint(request.getAmount());

        // 로그 남기기
        adminLogRepository.save(AdminLog.of(
                member,
                POINT_UPDATE,
                request.getReason(),
                adminName));
    }

    @Transactional(readOnly = true)
    public Page<AdminMemberPreviewDTO> searchMember(String name,
                                                    String email,
                                                    MemberStatus status,
                                                    Pageable pageable) {
        return adminMemberRepository.searchMember(name, email, status, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<AdminMemberDetailDTO> getMemberDetail(Long memberId) {
        return adminMemberRepository.getMemberDetail(memberId);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }
}
