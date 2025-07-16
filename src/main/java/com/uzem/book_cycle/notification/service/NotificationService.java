package com.uzem.book_cycle.notification.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.order.entity.Order;

import java.util.List;

public interface NotificationService {
    void notifyNextReservationIfExists(RentalBook rentalBook);
    void notifyRentalOverdue(RentalHistory rentalHistory);
    void notifyRentalOverdueFee(RentalHistory rentalHistory, long overdueDays);
    void notifyReturnDueReminder(Order order, Member member, List<RentalHistory> rentalHistories, String message);
    void notifyOrderShipped(Order order);
}
