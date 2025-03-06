package com.uzem.book_cycle.member.service;

import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.dto.MemberResponseDTO;
import com.uzem.book_cycle.member.dto.MemberUpdatePasswordRequestDTO;
import com.uzem.book_cycle.member.dto.MemberUpdateRequestDTO;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.SecurityContextService;
import com.uzem.book_cycle.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.uzem.book_cycle.member.type.MemberErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final SecurityContextService securityContextService;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;

    // 내정보 조회
    public MemberResponseDTO getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public MemberResponseDTO updateMyInfo(
            Long memberId, MemberUpdateRequestDTO requestDTO) {

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        member.updateMyInfo(requestDTO.getPhone(), requestDTO.getAddress());

        // SecurityContext 인증 정보 업데이트
        securityContextService.updateAuthentication(member);

        return MemberResponseDTO.from(member);
    }

    @Transactional
    public void updatePassword
            (Long memberId, MemberUpdatePasswordRequestDTO requestDTO,
             String accessToken) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        // 현재 비밀번호 확인
        validationUpdatePassword(requestDTO, member);

        String newPassword = passwordEncoder.encode(requestDTO.getNewPassword());
        member.updatePassword(newPassword);

        // 리프레시 토큰 삭제
        redisUtil.delete("refreshToken:" + memberId);

        long expiration = tokenProvider.getExpiration(accessToken);
        if(expiration > 0){
            redisUtil.setBlackList("blacklist:" + accessToken, "access_token", expiration);
        }

        // SecurityContext 초기화 (현재 세션 인증 정보 삭제)
        SecurityContextHolder.clearContext();

        log.info("비밀번호 변경 후 로그아웃 완료: {}", memberId);
    }

    private void validationUpdatePassword(MemberUpdatePasswordRequestDTO requestDTO, Member member) {
        // 현재 비밀번호 확인
        if(!passwordEncoder.matches(requestDTO.getCurrentPassword(), member.getPassword())) {
            throw new MemberException(INCORRECT_PASSWORD);
        }
        // 현재 비밀번호와 새 비밀번호 동일
        if(passwordEncoder.matches(requestDTO.getNewPassword(), member.getPassword())) {
            throw new MemberException(SAME_AS_CURRENT_PASSWORD);
        }

        // 새로운 비밀번호 일치 확인
        if(!Objects.equals(requestDTO.getNewPassword(), requestDTO.getConfirmPassword())) {
            throw new MemberException(CONFIRM_PASSWORD_MISMATCH);
        }

    }
}
