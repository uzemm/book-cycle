package com.uzem.book_cycle.admin.dto.member;

import com.uzem.book_cycle.member.type.MemberStatus;
import lombok.Getter;

@Getter
public class AdminMemberStatusUpdateRequest {

    private MemberStatus status;
}
