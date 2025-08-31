package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.admin.type.RentalErrorCode;
import com.uzem.book_cycle.admin.type.SalesErrorCode;
import com.uzem.book_cycle.cart.type.CartErrorCode;
import com.uzem.book_cycle.member.type.MemberErrorCode;
import com.uzem.book_cycle.order.type.OrderErrorCode;
import com.uzem.book_cycle.payment.type.PaymentErrorCode;
import com.uzem.book_cycle.security.token.TokenErrorCode;
import com.uzem.book_cycle.wish.type.WishErrorCode;
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
    public ResponseEntity<ErrorResponse> handleTokenException(TokenException e) {
        log.error("{} is occurred.", e.getTokenErrorCode());

        TokenErrorCode code = e.getTokenErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
        log.error("{} is occurred.", e.getMemberErrorCode());

        MemberErrorCode code = e.getMemberErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(SalesException.class)
    public ResponseEntity<ErrorResponse> handleSaleException(SalesException e) {
        log.error("{} is occurred.", e.getSalesErrorCode());

        SalesErrorCode code = e.getSalesErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        log.error("{} is occurred.", e.getPaymentErrorCode());

        PaymentErrorCode code = e.getPaymentErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(RentalException.class)
    public ResponseEntity<ErrorResponse> handleRentalException(RentalException e) {
        log.error("{} is occurred.", e.getRentalErrorCode());

        RentalErrorCode code = e.getRentalErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(WishException.class)
    public ResponseEntity<ErrorResponse> handleWishException(WishException e) {
        log.error("{} is occurred.", e.getWishErrorCode());

        WishErrorCode code = e.getWishErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<ErrorResponse> handleWishException(CartException e) {
        log.error("{} is occurred.", e.getCartErrorCode());

        CartErrorCode code = e.getCartErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> handleOrderException(OrderException e) {
        log.error("{} is occurred.", e.getOrderErrorCode());

        OrderErrorCode code = e.getOrderErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
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
