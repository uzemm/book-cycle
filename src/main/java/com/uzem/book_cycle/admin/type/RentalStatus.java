package com.uzem.book_cycle.admin.type;

public enum RentalStatus {
    AVAILABLE, RENTED,  OVERDUE, PENDING_PAYMENT, RETURNED, CANCELED;

    public boolean canReserve() {
        return this == RENTED || this == OVERDUE;
    }
}
