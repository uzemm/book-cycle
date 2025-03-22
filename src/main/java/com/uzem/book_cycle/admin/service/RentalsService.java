package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.rentals.RentalsBook;
import com.uzem.book_cycle.admin.dto.rentals.RentalsRequestDTO;
import com.uzem.book_cycle.admin.dto.rentals.RentalsResponseDTO;
import com.uzem.book_cycle.admin.dto.rentals.UpdateRentalsRequestDTO;
import com.uzem.book_cycle.admin.repository.RentalsRepository;
import com.uzem.book_cycle.exception.RentalsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalsErrorCode.RENTALS_BOOK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RentalsService {

    private final RentalsRepository rentalsRepository;

    public RentalsBook createRentalsBook(RentalsRequestDTO rentalsRequestDTO) {
        RentalsBook book = RentalsBook.from(rentalsRequestDTO);
        return rentalsRepository.save(book);
    }

    public RentalsResponseDTO getRentalsBookDetail(Long rentalId){
        RentalsBook rentalsBook = rentalsRepository.findById(rentalId).orElseThrow(
                () -> new RentalsException(RENTALS_BOOK_NOT_FOUND));

        return RentalsResponseDTO.create(rentalsBook);
    }

    @Transactional
    public void updateRentalsBook(Long rentalId, UpdateRentalsRequestDTO update) {
        RentalsBook rentalsBook = rentalsRepository.findById(rentalId).orElseThrow(
                () -> new RentalsException(RENTALS_BOOK_NOT_FOUND));

        rentalsBook.updateRentalsBook(update);
    }

    @Transactional
    public void deleteRentalsBook(Long rentalId) {
        RentalsBook rentalsBook = rentalsRepository.findByIdAndIsDeletedFalse(rentalId).orElseThrow(
                () -> new RentalsException(RENTALS_BOOK_NOT_FOUND));
        rentalsBook.delete();
    }

    public List<RentalsBook> searchRentalsBook(String keyword) {
        return rentalsRepository.searchByKeyword(keyword);
    }
}
