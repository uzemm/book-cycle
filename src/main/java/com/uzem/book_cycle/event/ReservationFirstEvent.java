package com.uzem.book_cycle.event;


import com.uzem.book_cycle.admin.entity.RentalBook;

public record ReservationFirstEvent(RentalBook rentalBook, String message) {

}
