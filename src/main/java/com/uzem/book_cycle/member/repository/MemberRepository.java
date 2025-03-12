package com.uzem.book_cycle.member.repository;

import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email); //이메일로 회원 조회
    boolean existsByEmail(String email); //이메일 중복 체크
    boolean existsByPhone(String phone); //전화번호 중복 체크
}
