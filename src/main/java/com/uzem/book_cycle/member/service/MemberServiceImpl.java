package com.uzem.book_cycle.member.service;

import com.uzem.book_cycle.auth.entity.EmailVerification;
import com.uzem.book_cycle.auth.repository.EmailVerificationRepository;
import com.uzem.book_cycle.auth.service.EmailService;
import com.uzem.book_cycle.rental.repository.RentalHistoryRepository;
import com.uzem.book_cycle.reservation.repository.ReservationRepository;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.dto.*;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.redis.RedisUtil;
import com.uzem.book_cycle.security.SecurityContextService;
import com.uzem.book_cycle.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.uzem.book_cycle.admin.type.RentalStatus.OVERDUE;
import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;
import static com.uzem.book_cycle.member.type.MemberErrorCode.*;
import static com.uzem.book_cycle.order.type.OrderStatus.*;
import static com.uzem.book_cycle.order.type.ShippingStatus.PREPARING;
import static com.uzem.book_cycle.order.type.ShippingStatus.SHIPPED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final SecurityContextService securityContextService;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;
    private final EmailVerificationRepository emailRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    // 내정보 미리보기
    @Transactional(readOnly = true)
    public MemberResponseDTO getMyInfo(Long memberId) {

        return MemberResponseDTO.from(getMember(memberId));
    }

    // 내정보 상세 조회
    @Transactional(readOnly = true)
    public MemberDetailResponseDTO getMyInfoDetail(Long memberId) {

        return MemberDetailResponseDTO.from(getMember(memberId));
    }

    public MemberDetailResponseDTO updateMyInfo(
            Long memberId, UpdateInfoRequestDTO requestDTO) {

        Member member = getMember(memberId);

        member.updateMyInfo(requestDTO.getPhone(), requestDTO.getAddress());

        // SecurityContext 인증 정보 업데이트
        securityContextService.updateAuthentication(member);

        return MemberDetailResponseDTO.from(member);
    }

    public void updatePassword
            (Long memberId, UpdatePasswordRequestDTO requestDTO,
             String accessToken) {
        Member member = getMember(memberId);

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

    private void validationUpdatePassword(UpdatePasswordRequestDTO requestDTO, Member member) {
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

    // 이메일 변경 요청
    public void updateEmail(Long memberId, UpdateEmailRequestDTO requestDTO) {
        Member member = getMember(memberId);

        if(memberRepository.findByEmail(requestDTO.getNewEmail()).isPresent()){
            throw new MemberException(DUPLICATE_EMAIL);
        }

        EmailVerification emailVerification = createEmailVerification(member, requestDTO);
        emailRepository.save(emailVerification);

        emailService.sendChangeEmailVerification(requestDTO.getNewEmail(), emailVerification.getVerificationCode());
    }

    //인증코드 생성
    private EmailVerification createEmailVerification(
            Member member, UpdateEmailRequestDTO requestDTO) {
        String verificationCode = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 8).toUpperCase();

        return EmailVerification.builder()
                .member(member)
                .email(requestDTO.getNewEmail())
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
    }

    // 인증코드 확인
    public MemberResponseDTO UpdateEmailCheck(
            String email, String verificationCode) {
        //인증코드를 찾는다
        EmailVerification emailVerification = emailRepository.
                findByEmailAndVerificationCode(email, verificationCode)
                .orElseThrow(() -> new MemberException(EMAIL_VERIFICATION_CODE_INVALID));

        validateEmailVerification(emailVerification);

        //회원 가져옴
        Member member = emailVerification.getMember();

        //회원상태 변경
        member.updateEmail(emailVerification.getEmail());

        //이메일 인증 완료
        emailVerification.verified();
        emailRepository.save(emailVerification);

        //인증 성공 시 데이터 삭제
        emailRepository.delete(emailVerification);

        return MemberResponseDTO.from(member);
    }

    // 인증 코드 만료 확인
    private static void validateEmailVerification(EmailVerification emailVerification) {
        if(emailVerification.isExpired()){
            throw new MemberException(EMAIL_VERIFICATION_CODE_EXPIRED);
        }
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = getMember(memberId);

        // 공통 검증
        validateDeletable(member);

        // 탈퇴 처리
        member.deleteMember();

        // refreshToken 삭제
        redisUtil.delete("refreshToken:" + memberId);
    }

    // 회원 탈퇴
    public void validateDeletable(Member member) {
        // 1. 대여 중(대여 + 연체)
        boolean hasRentals = rentalHistoryRepository
                .existsByMemberAndRentalStatusIn(member, List.of(RENTED, OVERDUE));
        if(hasRentals) throw new MemberException(MEMBER_HAS_ACTIVE_RENTALS);

        // 2. 예약 존재 (isActive && deadline > now)
        boolean hasReservations  = reservationRepository
                .existsByMemberAndIsActiveTrueAndPaymentDeadlineAfter(member, LocalDate.now());
        if(hasReservations ) throw new MemberException(MEMBER_HAS_ACTIVE_RESERVATIONS);

        // 3. 주문 미완료 (배송 준비/배송 중 포함)
        boolean hasOrders = orderRepository.existsByMemberAndOrderStatusInAndShippingStatusIn(
                member,
                List.of(PAID, CANCEL_REQUESTED, CANCEL_PENDING),
                List.of(PREPARING, SHIPPED));

        if(hasOrders) throw new MemberException(MEMBER_HAS_ACTIVE_DELIVERY);
    }

    // 주문 조회
    public List<MemberOrderPreviewDTO> getMyOrders(Long memberId){
        return orderRepository.findByMemberId(memberId).stream()
                .map(MemberOrderPreviewDTO::from)
                .toList();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }

}
