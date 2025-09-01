package com.uzem.book_cycle.rental.repository;

import com.uzem.book_cycle.admin.dto.rental.AdminRentalStatusDTO;
import com.uzem.book_cycle.admin.type.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface RentalHistoryRepositoryCustom {

    Page<AdminRentalStatusDTO> searchRentals(Long memberId,
                                             RentalStatus rentalStatus,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             Pageable pageable);
}
