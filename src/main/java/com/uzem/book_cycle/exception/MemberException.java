package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.member.type.MemberErrorCode;
import lombok.*;

@Getter
public class MemberException extends RuntimeException{
    private final MemberErrorCode memberErrorCode;

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage()); // 부모 클래스 message 설정
        this.memberErrorCode = memberErrorCode;
    }
}
