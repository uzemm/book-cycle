package com.uzem.book_cycle.member.controller;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.book.dto.*;
import com.uzem.book_cycle.book.entity.RentalHistory;
import com.uzem.book_cycle.book.repository.RentalHistoryRepository;
import com.uzem.book_cycle.book.service.RentalService;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.member.dto.*;
import com.uzem.book_cycle.member.service.MemberService;
import com.uzem.book_cycle.security.CustomUserDetails;
import com.uzem.book_cycle.security.token.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RentalService rentalService;
    private final AdminRentalRepository adminRentalRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    // 내정보
    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(memberService.getMyInfo(userDetails.getId()));
    }

    @PatchMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateInfoRequestDTO request) {

        Long memberId = userDetails.getId();
        MemberResponseDTO updateMyInfo =
                memberService.updateMyInfo(memberId, request);

        return ResponseEntity.ok(updateMyInfo);
    }

    @PatchMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyPassword(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody@Valid UpdatePasswordRequestDTO requestDTO){

        Long memberId = userDetails.getId();
        String accessToken = tokenProvider.resolveToken(request);

        memberService.updatePassword(memberId, requestDTO, accessToken);

        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    @PostMapping("/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody@Valid UpdateEmailRequestDTO requestDTO){

        Long memberId = userDetails.getId();
        memberService.updateEmail(memberId, requestDTO);

        return ResponseEntity.ok("이메일 변경 요청 성공");
    }

    @PatchMapping("/email/check")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> updateMyEmailCheck(
            @RequestBody@Valid UpdateEmailVerifyRequestDTO requestDTO) {

        MemberResponseDTO memberResponseDTO = memberService.UpdateEmailCheck(
                requestDTO.getNewEmail(), requestDTO.getVerificationCode());

        return ResponseEntity.ok(memberResponseDTO);
    }

    // 예약조회
    @GetMapping("/reservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<ReservationResponseDTO> myReservations =
                rentalService.getMyReservations(userDetails.getMember());
        return ResponseEntity.ok(myReservations);
    }

    // 예약취소
    @DeleteMapping("/reservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelMyReservation(
            @RequestBody ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        RentalBook rentalBook = adminRentalRepository.findById(requestDTO.getRentalBookId())
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));
                rentalService.cancelMyReservation(rentalBook, userDetails.getMember());

        return ResponseEntity.ok().body("예약 취소 완료");
    }

    // 대여 도서 조회
    @GetMapping("/rentals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalHistoryResponseDTO>> getMyRentals(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<RentalHistoryResponseDTO> myRentals = rentalService.getMyRentals(userDetails.getMember());
        return ResponseEntity.ok(myRentals);
    }

    // 연체 도서 조회
    @GetMapping("/overdues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalHistoryResponseDTO>> getMyOverdues(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<RentalHistoryResponseDTO> myOverdue = rentalService.getMyOverdue(userDetails.getMember());
        return ResponseEntity.ok(myOverdue);
    }

    // 대여 이력 조회
    @GetMapping("/rental-histories")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalHistoryResponseDTO>> getMyRentalHistories(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<RentalHistoryResponseDTO> myRentalHistories =
                rentalService.getMyRentalHistories(userDetails.getMember());
        return ResponseEntity.ok(myRentalHistories);
    }

    // 반납하기
    @PostMapping("/return")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalHistoryResponseDTO> returnMyRental(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RentalRequestDTO requestDTO){
        // 대여도서 조회
        RentalBook rentalBook = adminRentalRepository.findById(requestDTO.getRentalBookId())
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));
        // 대여이력 조회
        RentalHistory rentalHistory = rentalHistoryRepository
                .findByRentalBookAndMember(rentalBook, userDetails.getMember())
                .orElseThrow(() -> new RentalException(RENTAL_HISTORY_NOT_FOUND));
        RentalHistoryResponseDTO rentalHistoryResponseDTO = rentalService.returnRental(
                userDetails.getMember(), requestDTO.getPayment(), rentalHistory);

        return ResponseEntity.ok(rentalHistoryResponseDTO);
    }

    // 결제 대기 취소
    @DeleteMapping("/reservations/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalResponseDTO> cancelPendingRental(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RentalRequestDTO requestDTO){
        // 대여도서 조회
        RentalBook rentalBook = adminRentalRepository.findById(requestDTO.getRentalBookId())
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        RentalResponseDTO rentalResponseDTO = rentalService.cancelPendingPayment(
                rentalBook, userDetails.getMember());

        return ResponseEntity.ok(rentalResponseDTO);
    }
}
