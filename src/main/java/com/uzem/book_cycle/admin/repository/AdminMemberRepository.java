package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMemberRepository extends JpaRepository<Member, Long>, AdminMemberRepositoryCustom {
}
