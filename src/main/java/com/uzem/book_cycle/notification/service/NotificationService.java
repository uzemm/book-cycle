package com.uzem.book_cycle.notification.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.order.entity.Order;

public interface NotificationService {
    void notifyNextReservationIfExists(RentalBook rentalBook);
    void notifyRentalOverdue(RentalHistory rentalHistory);
    void notifyRentalOverdueFee(RentalHistory rentalHistory, long overdueDays);
    void notifyReturnDue(RentalBook rentalBook);
    void notifyOrderShipped(Order order);
}
