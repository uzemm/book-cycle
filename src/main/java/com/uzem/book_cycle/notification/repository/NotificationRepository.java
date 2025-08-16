package com.uzem.book_cycle.notification.repository;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.entity.Notification;
import com.uzem.book_cycle.notification.type.NotificationType;
import com.uzem.book_cycle.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByMemberAndRentalBookAndOverdueDay(Member member,
                                                     RentalBook rentalBook,
                                                     Integer overdueDay);

    boolean existsByMemberAndRentalBookAndType(Member member,
                                               RentalBook rentalBook,
                                               NotificationType type);

    boolean existsByMemberAndOrderAndType(Member member,
                                               Order order,
                                               NotificationType type);
}

