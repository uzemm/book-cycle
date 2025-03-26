package com.uzem.book_cycle.security;

import com.uzem.book_cycle.exception.MemberException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.uzem.book_cycle.member.type.MemberErrorCode.UNAUTHORIZED;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new MemberException(UNAUTHORIZED);
        }
        return Long.valueOf(authentication.getName());
    }
}
