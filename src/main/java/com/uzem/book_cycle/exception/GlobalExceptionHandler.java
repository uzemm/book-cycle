package com.uzem.book_cycle.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenException.class)
    public ErrorResponse handleTokenException(TokenException e) {
        log.error("{} is occurred.", e.getTokenErrorCode());

        return new ErrorResponse(e.getTokenErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MemberException.class)
    public ErrorResponse handleMemberException(MemberException e) {
        log.error("{} is occurred.", e.getMemberErrorCode());

        return new ErrorResponse(e.getMemberErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(SalesException.class)
    public ErrorResponse handleSaleException(SalesException e) {
        log.error("{} is occurred.", e.getSalesErrorCode());

        return new ErrorResponse(e.getSalesErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ErrorResponse handlePaymentException(PaymentException e) {
        log.error("{} is occurred.", e.getPaymentErrorCode());

        return new ErrorResponse(e.getPaymentErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(RentalException.class)
    public ErrorResponse handleRentalException(RentalException e) {
        log.error("{} is occurred.", e.getRentalErrorCode());

        return new ErrorResponse(e.getRentalErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(WishException.class)
    public ErrorResponse handleWishException(WishException e) {
        log.error("{} is occurred.", e.getWishErrorCode());

        return new ErrorResponse(e.getWishErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(CartException.class)
    public ErrorResponse handleWishException(CartException e) {
        log.error("{} is occurred.", e.getCartErrorCode());

        return new ErrorResponse(e.getCartErrorCode().getCode(), e.getErrorMessage());
    }

    @ExceptionHandler(OrderException.class)
    public ErrorResponse handleOrderException(OrderException e) {
        log.error("{} is occurred.", e.getOrderErrorCode());

        return new ErrorResponse(e.getOrderErrorCode().getCode(), e.getErrorMessage());
    }

    // 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // 인증 실패
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패: " + ex.getMessage());
    }

    // 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    // 찾을 수 없는 데이터
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("데이터를 찾을 수 없습니다.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("JSON 형식 오류: " + ex.getMessage());
    }
}
