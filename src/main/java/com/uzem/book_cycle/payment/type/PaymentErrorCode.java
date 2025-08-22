package com.uzem.book_cycle.payment.type;

import com.uzem.book_cycle.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_NOT_FOUND("결제내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TOSS_PAYMENT_REQUEST_FAILED("토스 결제 요청 실패", HttpStatus.BAD_GATEWAY),
    PAYMENT_SESSION_MISMATCH("세션의 결제 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED("결제 요청이 실패했습니다. 다시 시도해주세요.", HttpStatus.BAD_REQUEST),
    PAYMENT_TIMEOUT("결제 요청 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT),
    PAYMENT_CANCEL_FAILED("결제 취소에 실패했습니다.", HttpStatus.CONFLICT),
    INVALID_REQUEST("잘못된 결제 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    SYSTEM_ERROR("시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR)
    ;


    private final String description;
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.description;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    // 토스 원본 코드 -> 내부 코드 매핑
    public static PaymentErrorCode fromTossCode(String tossCode) {
        return switch (tossCode) {
            case "PAY_PROCESS_CANCELED", "PAY_PROCESS_ABORTED" ->
                    PAYMENT_FAILED;
            case "NOT_FOUND_PAYMENT_SESSION" -> PAYMENT_TIMEOUT;
            case "INVALID_REQUEST" -> PAYMENT_CANCEL_FAILED;
            case "UNAUTHORIZED_KEY" -> UNAUTHORIZED;
            default -> SYSTEM_ERROR;
        };
    }
}
