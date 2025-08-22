package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.rental.AdminRentalRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalResponseDTO;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalStatusDTO;
import com.uzem.book_cycle.admin.dto.rental.UpdateAdminRentalRequestDTO;
import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.type.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminRentalService {
    RentalBook createRentalBook(AdminRentalRequestDTO rentalRequestDTO);
    AdminRentalResponseDTO getRentalBookDetail(Long rentalId);
    void updateRentalBook(Long rentalId, UpdateAdminRentalRequestDTO update);
    void deleteRentalBook(Long rentalId);
    Page<AdminRentalStatusDTO> searchRentals(Long memberId,
                                             RentalStatus rentalStatus,
                                             LocalDate rentalDate,
                                             LocalDate returnDate,
                                             Pageable pageable);
}
