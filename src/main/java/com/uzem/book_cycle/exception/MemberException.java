package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.member.type.MemberErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberException extends RuntimeException{
    private MemberErrorCode memberErrorCode;
    private String errorMessage;

    public MemberException(MemberErrorCode memberErrorCode) {
        this.memberErrorCode = memberErrorCode;
        this.errorMessage = memberErrorCode.getDescription();
    }

}
