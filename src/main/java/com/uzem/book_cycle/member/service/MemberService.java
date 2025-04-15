package com.uzem.book_cycle.member.service;

import com.uzem.book_cycle.member.dto.MemberResponseDTO;
import com.uzem.book_cycle.member.dto.UpdateEmailRequestDTO;
import com.uzem.book_cycle.member.dto.UpdateInfoRequestDTO;
import com.uzem.book_cycle.member.dto.UpdatePasswordRequestDTO;

public interface MemberService {
    MemberResponseDTO getMyInfo(Long memberId);
    MemberResponseDTO updateMyInfo(
            Long memberId, UpdateInfoRequestDTO requestDTO);
    void updatePassword
            (Long memberId, UpdatePasswordRequestDTO requestDTO,
             String accessToken);
    void updateEmail(Long memberId, UpdateEmailRequestDTO requestDTO);
    MemberResponseDTO UpdateEmailCheck(
            String email, String verificationCode);
    void deleteMember(Long memberId);
}
