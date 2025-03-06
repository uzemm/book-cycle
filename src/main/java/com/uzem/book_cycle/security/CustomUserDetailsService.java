package com.uzem.book_cycle.security;

import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        return new CustomUserDetails(member);
    }

}
