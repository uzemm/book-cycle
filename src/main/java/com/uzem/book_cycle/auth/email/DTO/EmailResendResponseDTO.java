package com.uzem.book_cycle.auth.email.DTO;

import com.uzem.book_cycle.member.type.MemberStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class EmailResendResponseDTO {
    private String email;
    private MemberStatus status;
    private LocalDateTime expiresAt;
}
