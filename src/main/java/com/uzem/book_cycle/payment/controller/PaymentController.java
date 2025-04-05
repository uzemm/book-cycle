package com.uzem.book_cycle.payment.controller;

import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.exception.PaymentException;
import com.uzem.book_cycle.payment.dto.CancelRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.payment.dto.SaveAmountRequest;
import com.uzem.book_cycle.payment.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.uzem.book_cycle.payment.type.PaymentErrorCode.PAYMENT_SESSION_MISMATCH;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public String payment() {
        return "payment";
    }


    @PostMapping("/saveAmount")
    @ResponseBody
    public ResponseEntity<String> saveAmount(HttpSession session, @RequestBody SaveAmountRequest request) {
        /**
         * 결제를 요청하기 전에 orderId, amount를 서버에 저장
         * 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도
         */
        session.setAttribute("orderId", request.getOrderId());
        session.setAttribute("amount", request.getAmount());
        return ResponseEntity.ok("Payment save success");
    }

    // 결제 요청
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> requestPayment(@RequestBody PaymentRequestDTO request,
                                            HttpSession session) {
        try{
            // 세션에 저장된 값 가져오기
            String savedOrderId = (String) session.getAttribute("orderId");
            Long savedAmount = (Long) session.getAttribute("amount");

            // 검증
            if (!request.getTossOrderId().equals(savedOrderId) || !request.getAmount().equals(savedAmount)) {
                throw new PaymentException(PAYMENT_SESSION_MISMATCH);
            }
            // 결제 승인 요청
            PaymentResponseDTO response = paymentService.processPayment(request);
            return  ResponseEntity.ok(response);
        } catch (OrderException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getErrorMessage(), "code", e.getOrderErrorCode()));
        }
    }

    // 결제 취소
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<PaymentResponseDTO> cancelPayment(
            @RequestBody @Valid CancelRequestDTO request) {
        PaymentResponseDTO cancelResponse = paymentService.processCancelPayment(request);
        return ResponseEntity.ok(cancelResponse);
    }

    // 결제 조회
    @GetMapping("/detail")
    public ResponseEntity<PaymentResponseDTO> getPayment(@RequestParam String paymentKey) {
        PaymentResponseDTO response = paymentService.getPayment(paymentKey);
        return ResponseEntity.ok(response);
    }
}
