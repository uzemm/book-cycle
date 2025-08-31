package com.uzem.book_cycle.auth.email.repository;

import com.uzem.book_cycle.auth.email.entity.EmailVerification;
import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
    void delete(EmailVerification emailVerification);
    Integer deleteAllByExpiresAtBefore(LocalDateTime localDateTime);
    void deleteAllByMemberIn(List<Member> members);
}
