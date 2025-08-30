package com.uzem.book_cycle.notification.entity;

import com.uzem.book_cycle.rental.entity.RentalBook;
import com.uzem.book_cycle.entity.BaseEntity;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.notification.type.NotificationType;
import com.uzem.book_cycle.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(
        name = "notification",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_member_order_type",
                        columnNames = {"member_id", "order_id", "type"}
                ),
                @UniqueConstraint(
                        name = "uk_notification_member_rentalbook_type",
                        columnNames = {"member_id", "rental_book_id", "type"}
                )
        }
)
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_book_id")
    private RentalBook rentalBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead; // false

    private Integer overdueDay; // nullable, 연체알림일 경우에만

}
