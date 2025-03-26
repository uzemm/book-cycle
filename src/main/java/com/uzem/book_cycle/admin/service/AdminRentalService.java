package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.rental.RentalBook;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalResponseDTO;
import com.uzem.book_cycle.admin.dto.rental.UpdateAdminRentalRequestDTO;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.exception.RentalException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.RENTAL_BOOK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminRentalService {

    private final AdminRentalRepository rentalRepository;

    public RentalBook createRentalBook(AdminRentalRequestDTO rentalRequestDTO) {
        RentalBook book = RentalBook.from(rentalRequestDTO);
        return rentalRepository.save(book);
    }

    public AdminRentalResponseDTO getRentalBookDetail(Long rentalId){
        RentalBook rentalBook = rentalRepository.findById(rentalId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        return AdminRentalResponseDTO.create(rentalBook);
    }

    @Transactional
    public void updateRentalBook(Long rentalId, UpdateAdminRentalRequestDTO update) {
        RentalBook rentalBook = rentalRepository.findById(rentalId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        rentalBook.updateRentalBook(update);
    }

    @Transactional
    public void deleteRentalBook(Long rentalId) {
        RentalBook rentalBook = rentalRepository.findByIdAndIsDeletedFalse(rentalId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));
        rentalBook.delete();
    }

    public List<RentalBook> searchRentalBook(String keyword) {
        return rentalRepository.searchByKeyword(keyword);
    }
}
