package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.admin.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
    List<AdminLog> findByMemberId(Long memberId);
}