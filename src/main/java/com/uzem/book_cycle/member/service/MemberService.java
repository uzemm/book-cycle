package com.uzem.book_cycle.member.service;

import com.uzem.book_cycle.member.dto.*;

import java.util.List;

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
    List<MemberOrderPreviewDTO> getMyOrders(Long memberId);
}
