package com.uzem.book_cycle.payment.repository;

import com.uzem.book_cycle.payment.entity.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CancelRepository extends JpaRepository<Cancel, Long> {
}
