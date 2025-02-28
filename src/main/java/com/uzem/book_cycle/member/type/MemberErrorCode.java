package com.uzem.book_cycle.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {
    EMAIL_ALREADY_IN_USE("이미 가입된 이메일입니다."),
    EMAIL_SEND_FAILED("이메일 전송에 실패했습니다."),
    EMAIL_VERIFICATION_CODE_INVALID("잘못된 인증 코드입니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED("인증코드가 만료되었습니다."),
    EMAIL_ALREADY_VERIFIED("인증이 이미 완료되었습니다."),
    PHONE_ALREADY_IN_USE("이미 가입된 전화번호입니다."),
    INCORRECT_ID_OR_PASSWORD("아이디 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다."),
    UNAUTHORIZED("권한이 없습니다.")
    ;

    private String description;
}
