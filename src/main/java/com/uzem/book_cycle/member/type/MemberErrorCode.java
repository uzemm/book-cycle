package com.uzem.book_cycle.member.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    EMAIL_ALREADY_IN_USE("이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    EMAIL_SEND_FAILED("이메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_VERIFICATION_CODE_INVALID("잘못된 인증 코드입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_CODE_EXPIRED("인증코드가 만료되었습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED("인증이 이미 완료되었습니다.", HttpStatus.BAD_REQUEST),

    PHONE_ALREADY_IN_USE("이미 가입된 전화번호입니다.", HttpStatus.CONFLICT),

    INCORRECT_ID_OR_PASSWORD("아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    INCORRECT_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SAME_AS_CURRENT_PASSWORD("현재 비밀번호와 새 비밀번호가 동일합니다.", HttpStatus.BAD_REQUEST),
    CONFIRM_PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다.", HttpStatus.FORBIDDEN),
    DUPLICATE_EMAIL("이미 사용 중인 이메일입니다. 다른 이메일을 입력하세요.", HttpStatus.CONFLICT),

    INSUFFICIENT_POINTS("보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("권한이 없습니다.", HttpStatus.UNAUTHORIZED),

    MEMBER_HAS_ACTIVE_RENTALS("대여 중인 도서가 있으므로 탈퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_HAS_ACTIVE_RESERVATIONS("예약 중인 도서가 있으므로 탈퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_HAS_ACTIVE_DELIVERY("배송 중인 주문이 있으므로 탈퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_HAS_ACTIVE_ORDER("주문 중인 도서가 있으므로 탈퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_AUTHORITY_NOT_FOUND("권한 정보가 없습니다.", HttpStatus.UNAUTHORIZED)
    ;

    private final String description;
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
