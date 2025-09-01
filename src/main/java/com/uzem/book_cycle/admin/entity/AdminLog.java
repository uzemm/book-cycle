package com.uzem.book_cycle.admin.entity;

import com.uzem.book_cycle.admin.type.LogActionType;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class AdminLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 어떤 회원 대상 로그인지

    @Column(nullable = false, length = 50)
    private LogActionType action;  // "POINT_UPDATE", "STATUS_CHANGE", "RESET_PASSWORD" 등

    @Column(length = 255)
    private String reason;  // 사유 (관리자 입력)

    @Column(nullable = false, length = 50)
    private String adminName; // 작업한 관리자 (name, id 등)

    // 정적 팩토리 메서드
    public static AdminLog of(Member member, LogActionType action, String reason, String adminName) {
        return AdminLog.builder()
                .member(member)
                .action(action)
                .reason(reason)
                .adminName(adminName)
                .build();
    }
}
