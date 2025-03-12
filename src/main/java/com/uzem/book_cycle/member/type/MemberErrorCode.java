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
    ;

    private String description;
}
