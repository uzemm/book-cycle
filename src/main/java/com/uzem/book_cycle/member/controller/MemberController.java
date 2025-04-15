package com.uzem.book_cycle.member.controller;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.book.dto.*;
import com.uzem.book_cycle.book.service.RentalService;
import com.uzem.book_cycle.exception.RentalException;
import com.uzem.book_cycle.member.dto.*;
import com.uzem.book_cycle.member.service.MemberService;
import com.uzem.book_cycle.security.CustomUserDetails;
import com.uzem.book_cycle.security.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회원 API", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RentalService rentalService;
    private final AdminRentalRepository adminRentalRepository;

    @Operation(summary = "내정보 조회")
    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(memberService.getMyInfo(userDetails.getId()));
    }

    @Operation(summary = "내정보 수정")
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

    @Operation(summary = "비밀번호 변경")
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

    @Operation(summary = "이메일 변경")
    @PostMapping("/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody@Valid UpdateEmailRequestDTO requestDTO){

        Long memberId = userDetails.getId();
        memberService.updateEmail(memberId, requestDTO);

        return ResponseEntity.ok("이메일 변경 요청 성공");
    }

    @Operation(summary = "이메일 인증번호 확인")
    @PatchMapping("/email/check")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberResponseDTO> updateMyEmailCheck(
            @RequestBody@Valid UpdateEmailVerifyRequestDTO requestDTO) {

        MemberResponseDTO memberResponseDTO = memberService.UpdateEmailCheck(
                requestDTO.getNewEmail(), requestDTO.getVerificationCode());

        return ResponseEntity.ok(memberResponseDTO);
    }

    @Operation(summary = "예약도서 조회")
    @GetMapping("/reservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<ReservationResponseDTO> myReservations =
                rentalService.getMyReservations(userDetails.getId());
        return ResponseEntity.ok(myReservations);
    }

    @Operation(summary = "예약도서 취소")
    @DeleteMapping("/reservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelMyReservation(
            @RequestBody ReservationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        RentalBook rentalBook = adminRentalRepository.findById(requestDTO.getRentalBookId())
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));
                rentalService.cancelMyReservation(rentalBook, userDetails.getId());

        return ResponseEntity.ok().body("예약 취소 완료");
    }

    @Operation(summary = "대여도서 조회")
    @GetMapping("/rentals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalHistoryResponseDTO>> getMyRentals(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<RentalHistoryResponseDTO> myRentals = rentalService.getMyRentals(userDetails.getId());
        return ResponseEntity.ok(myRentals);
    }

    @Operation(summary = "연체도서 조회")
    @GetMapping("/overdues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OverdueListResponseDTO>> getMyOverdues(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<OverdueListResponseDTO> myOverdue = rentalService.getMyOverdue(userDetails.getId());
        return ResponseEntity.ok(myOverdue);
    }

    @Operation(summary = "대여이력 조회")
    @GetMapping("/rental-histories")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalHistoryListResponseDTO>> getMyRentalHistories(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<RentalHistoryListResponseDTO> myRentalHistories =
                rentalService.getMyRentalHistories(userDetails.getId());
        return ResponseEntity.ok(myRentalHistories);
    }

    @Operation(summary = "대여도서 반납하기")
    @PostMapping("/return/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GroupReturnResponseDTO> returnMyRental(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RentalRequestDTO requestDTO,
            @PathVariable Long orderId){

        GroupReturnResponseDTO returnResponseDTO = rentalService.returnRental(
                orderId,
                userDetails.getId(), requestDTO.getPayment());

        return ResponseEntity.ok(returnResponseDTO);
    }

    @Operation(summary = "결제대기도서 취소")
    @DeleteMapping("/reservations/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalResponseDTO> cancelPendingRental(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RentalRequestDTO requestDTO){
        // 대여도서 조회
        RentalBook rentalBook = adminRentalRepository.findById(requestDTO.getRentalBookId())
                .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));

        RentalResponseDTO rentalResponseDTO = rentalService.cancelPendingPayment(
                rentalBook, userDetails.getId());

        return ResponseEntity.ok(rentalResponseDTO);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMember(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        memberService.deleteMember(userDetails.getId());
        return ResponseEntity.noContent().build(); // 204
    }
}
