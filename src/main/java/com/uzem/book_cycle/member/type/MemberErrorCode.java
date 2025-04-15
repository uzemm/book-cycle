package com.uzem.book_cycle.member.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    EMAIL_ALREADY_IN_USE("이미 가입된 이메일입니다."),
    EMAIL_SEND_FAILED("이메일 전송에 실패했습니다."),
    EMAIL_VERIFICATION_CODE_INVALID("잘못된 인증 코드입니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED("인증코드가 만료되었습니다."),
    EMAIL_ALREADY_VERIFIED("인증이 이미 완료되었습니다."),
    PHONE_ALREADY_IN_USE("이미 가입된 전화번호입니다."),
    INCORRECT_ID_OR_PASSWORD("아이디 또는 비밀번호가 올바르지 않습니다."),
    INCORRECT_PASSWORD("현재 비밀번호가 올바르지 않습니다."),
    SAME_AS_CURRENT_PASSWORD("현재 비밀번호와 새 비밀번호가 동일합니다."),
    CONFIRM_PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다."),
    DUPLICATE_EMAIL("이미 사용 중인 이메일입니다. 다른 이메일을 입력하세요."),
    INSUFFICIENT_POINTS("보유 포인트가 부족합니다."),
    UNAUTHORIZED("권한이 없습니다."),
    MEMBER_HAS_ACTIVE_RENTALS("대여 중인 도서가 있으므로 탈퇴할 수 없습니다."),
    MEMBER_HAS_ACTIVE_RESERVATIONS("예약 중인 도서가 있으므로 탈퇴할 수 없습니다."),
    MEMBER_HAS_ACTIVE_DELIVERY("배송 중인 주문이 있으므로 탈퇴할 수 없습니다."),
    ;

    private String description;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }
}
