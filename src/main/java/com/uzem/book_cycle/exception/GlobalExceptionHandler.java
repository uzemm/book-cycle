package com.uzem.book_cycle.exception;

import com.uzem.book_cycle.admin.type.RentalErrorCode;
import com.uzem.book_cycle.admin.type.SalesErrorCode;
import com.uzem.book_cycle.cart.type.CartErrorCode;
import com.uzem.book_cycle.common.ApiResponse;
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

        TokenErrorCode code = e.getTokenErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberException(MemberException e) {

        return ResponseEntity
                .status(e.getMemberErrorCode().getHttpStatus())
                .body(ApiResponse.error(e.getMemberErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(SalesException.class)
    public ResponseEntity<ErrorResponse> handleSaleException(SalesException e) {

        SalesErrorCode code = e.getSalesErrorCode();

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ErrorResponse(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentException(PaymentException e) {
        PaymentErrorCode code = e.getPaymentErrorCode();

        // 비즈니스 예외는 warn, 시스템 오류성 예외는 error로 구분 가능
        if(code.isSystemError()){
            log.error("Payment error occurred. code={}, message={}, originalMessage={}",
                    code.getCode(), code.getMessage(), e.getOriginalMessage());
        } else{
            log.error("Payment business error occurred. code={}, message={}, originalMessage={}",
                    code.getCode(), code.getMessage(), e.getOriginalMessage());
        }

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(RentalException.class)
    public ResponseEntity<ApiResponse<Void>> handleRentalException(RentalException e) {
        return ResponseEntity
                .status(e.getRentalErrorCode().getHttpStatus())
                .body(ApiResponse.error(e.getRentalErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(WishException.class)
    public ResponseEntity<ApiResponse<Void>> handleWishException(WishException e) {
        return ResponseEntity
                .status(e.getWishErrorCode().getHttpStatus())
                .body(ApiResponse.error(e.getWishErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartException(CartException e) {
        return ResponseEntity
                .status(e.getCartErrorCode().getHttpStatus())
                .body(ApiResponse.error(e.getCartErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderException(OrderException e) {
        return ResponseEntity
                .status(e.getOrderErrorCode().getHttpStatus())
                .body(ApiResponse.error(e.getOrderErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "시스템 오류가 발생했습니다."));
    }

    // 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", errors.toString()));
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_ARGUMENT", ex.getMessage()));
    }

    // 인증 실패
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("UNAUTHORIZED", "인증 실패: " + ex.getMessage()));
    }

    // 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("FORBIDDEN", "권한이 없습니다."));
    }

    // 찾을 수 없는 데이터
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("NOT_FOUND", "데이터를 찾을 수 없습니다."));
    }

    // JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("JSON_PARSE_ERROR", "JSON 형식 오류: " + ex.getMessage()));
    }

}
