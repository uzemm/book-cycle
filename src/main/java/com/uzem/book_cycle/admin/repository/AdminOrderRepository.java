package com.uzem.book_cycle.admin.repository;

import com.uzem.book_cycle.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminOrderRepository extends JpaRepository<Order, Long>, AdminOrderRepositoryCustom {
}
