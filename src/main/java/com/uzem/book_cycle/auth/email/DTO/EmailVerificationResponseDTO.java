package com.uzem.book_cycle.auth.email.DTO;

import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.type.MemberStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationResponseDTO {

    private String email;
    private MemberStatus status;

    public static EmailVerificationResponseDTO from(Member member,
                                                    EmailVerification verification) {
        return new EmailVerificationResponseDTO(
                member.getEmail(),
                member.getStatus());

    }
}
