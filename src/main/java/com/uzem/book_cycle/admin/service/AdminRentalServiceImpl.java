package com.uzem.book_cycle.admin.service;

import com.uzem.book_cycle.admin.dto.rental.AdminRentalStatusDTO;
import com.uzem.book_cycle.book.entity.RentalBook;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalRequestDTO;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalResponseDTO;
import com.uzem.book_cycle.admin.dto.rental.UpdateAdminRentalRequestDTO;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.exception.RentalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.RENTAL_BOOK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminRentalServiceImpl implements AdminRentalService {

    private final AdminRentalRepository adminRentalRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    @Transactional
    public RentalBook createRentalBook(AdminRentalRequestDTO rentalRequestDTO) {
        RentalBook book = RentalBook.from(rentalRequestDTO);
        return adminRentalRepository.save(book);
    }

    @Transactional(readOnly = true)
    public AdminRentalResponseDTO getRentalBookDetail(Long rentalId){
        RentalBook rentalBook = adminRentalRepository.findById(rentalId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        return AdminRentalResponseDTO.create(rentalBook);
    }

    @Transactional
    public void updateRentalBook(Long rentalId, UpdateAdminRentalRequestDTO update) {
        RentalBook rentalBook = adminRentalRepository.findById(rentalId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        rentalBook.updateRentalBook(update);
    }

    @Transactional
    public void deleteRentalBook(Long rentalId) {
        RentalBook rentalBook = adminRentalRepository.findByIdAndIsDeletedFalse(rentalId).orElseThrow(
                () -> new RentalException(RENTAL_BOOK_NOT_FOUND));
        rentalBook.delete();
    }

    @Transactional
    public List<RentalBook> searchRentalBook(String keyword) {
        return adminRentalRepository.searchByKeyword(keyword);
    }

    // 대여현황 조회
    @Transactional(readOnly = true)
    public Page<AdminRentalStatusDTO> searchRentals(Long memberId,
                                                    RentalStatus rentalStatus,
                                                    LocalDate rentalDate,
                                                    LocalDate returnDate,
                                                    Pageable pageable){
        return rentalHistoryRepository.searchRentals(
                memberId, rentalStatus, rentalDate, returnDate,pageable);
    }
}
