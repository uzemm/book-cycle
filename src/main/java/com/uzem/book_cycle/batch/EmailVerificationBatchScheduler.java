package com.uzem.book_cycle.batch;

import com.uzem.book_cycle.auth.email.repository.EmailVerificationRepository;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.uzem.book_cycle.member.type.MemberStatus.PENDING;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class EmailVerificationBatchScheduler {

    private final EmailVerificationRepository emailRepository;
    private final MemberRepository memberRepository;
    private static final long UNVERIFIED_MEMBER_EXPIRY_DAYS = 3;

    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteExpiredEmailVerificationRecords(){
        LocalDateTime now = LocalDateTime.now();
        int count = emailRepository.deleteAllByExpiresAtBefore(now);
        log.info("만료된 이메일 인증 {}건 삭제 완료 (기준일: {})", count, now);
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void deletePendingMembers (){
        LocalDateTime expiredAt = LocalDateTime.now().minusDays(
                UNVERIFIED_MEMBER_EXPIRY_DAYS);
        List<Member> members = memberRepository
                .findAllByStatusAndCreatedAtBefore(PENDING, expiredAt);

        if(!members.isEmpty()){
            emailRepository.deleteAllByMemberIn(members);
            memberRepository.deleteAll(members);
            log.info("미인증 회원 {}명 삭제 완료 (기준일: {})", members.size(), expiredAt);
        }
    }
}
