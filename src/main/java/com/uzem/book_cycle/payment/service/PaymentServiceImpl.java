package com.uzem.book_cycle.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.exception.PaymentException;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.payment.dto.CancelRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.payment.entity.Cancel;
import com.uzem.book_cycle.payment.entity.TossPayment;
import com.uzem.book_cycle.payment.repository.CancelRepository;
import com.uzem.book_cycle.payment.repository.PaymentRepository;
import com.uzem.book_cycle.payment.type.PaymentPurpose;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.order.type.OrderErrorCode.ORDER_NOT_FOUND;
import static com.uzem.book_cycle.payment.type.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static com.uzem.book_cycle.payment.type.PaymentErrorCode.TOSS_PAYMENT_REQUEST_FAILED;
import static com.uzem.book_cycle.payment.type.PaymentPurpose.OVERDUE;
import static com.uzem.book_cycle.payment.type.PaymentStatus.CANCELED;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final String SECRET_KEY = "토스 시크릿키";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CancelRepository cancelRepository;

    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        PaymentResponseDTO tossResponse = requestTossApi(request);
        Order order = findByTossOrderId(request.getTossOrderId());
        TossPayment tossPayment = createTossPayment(tossResponse, order);
        orderRepository.save(order);
        paymentRepository.save(tossPayment);
        return PaymentResponseDTO.from(tossPayment);
    }

    // 연체
    @Transactional
    public PaymentResponseDTO processOverduePayment(PaymentRequestDTO request) {
        PaymentResponseDTO tossResponse = requestTossApi(request);
        Order order = findByTossOrderId(request.getTossOrderId());
        TossPayment tossPayment = createTossPayment(tossResponse, order, OVERDUE);
        paymentRepository.save(tossPayment);
        return PaymentResponseDTO.from(tossPayment);
    }

    @Transactional
    public PaymentResponseDTO processCancelPayment(CancelRequestDTO request) {
        PaymentResponseDTO tossResponse = requestCancelTossApi(request);
        TossPayment tossPayment = paymentRepository.findByPaymentKey(request.getPaymentKey()).orElseThrow(
                () -> new OrderException(ORDER_NOT_FOUND));
        List<Cancel> cancels = createCancelPayment(request, tossResponse, tossPayment);

        tossPayment.setStatus(CANCELED);
        for (Cancel cancel : cancels) {
            tossPayment.addCancel(cancel);
            cancelRepository.save(cancel);
        }

        paymentRepository.save(tossPayment);
        return PaymentResponseDTO.from(tossPayment);
    }

    // 토스 승인 api 요청
    public PaymentResponseDTO requestTossApi(PaymentRequestDTO request) {
        try{
            String apiUrl = "https://api.tosspayments.com/v1/payments/confirm";
            // 응답 바디 설정
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("paymentKey", request.getPaymentKey());
            requestBody.put("orderId", request.getTossOrderId());
            requestBody.put("amount", request.getAmount());
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8)));
            headers.set("Idempotency-Key", UUID.randomUUID().toString());

            // 요청 보내기
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<PaymentResponseDTO> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, PaymentResponseDTO.class);

            System.out.println("Toss 응답 상태 코드: " + response.getStatusCode());
            System.out.println("응답: " + response.getBody());

            return response.getBody();

        } catch (HttpClientErrorException e) {
            // 토스 에러 json 파싱
            log.error("토스 요청 오류 발생 : {}", e.getResponseBodyAsString());
            throw new PaymentException(TOSS_PAYMENT_REQUEST_FAILED);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 취소 요청
    public PaymentResponseDTO requestCancelTossApi(CancelRequestDTO request) {
        try{
            String apiUrl = "https://api.tosspayments.com/v1/payments/" +
                    request.getPaymentKey() + "/cancel";

            // 응답 바디 설정
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("cancelReason", request.getCancelReason());
            requestBody.put("cancelAmount", request.getCancelAmount());
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8)));
            headers.set("Idempotency-Key", UUID.randomUUID().toString());

            // 요청 보내기
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<PaymentResponseDTO> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, PaymentResponseDTO.class);

            return response.getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // TossOrderId로 주문 조회
    private Order findByTossOrderId(String tossOrderId) {
        return orderRepository.findByTossOrderId(tossOrderId).orElseThrow(
                () -> new OrderException(ORDER_NOT_FOUND));
    }

    // TossPayment 생성
    private static TossPayment createTossPayment(PaymentResponseDTO tossResponse,
                                                 Order order, PaymentPurpose paymentPurpose) {
        TossPayment tossPayment = TossPayment.builder()
                .paymentKey(tossResponse.getPaymentKey())
                .tossOrderId(tossResponse.getOrderId())
                .order(order)
                .amount(order.getTotalPrice())
                .orderName(tossResponse.getOrderName())
                .requestedAt(tossResponse.getRequestedAt())
                .approvedAt(tossResponse.getApprovedAt())
                .status(tossResponse.getStatus())
                .type(tossResponse.getType())
                .paymentPurpose(paymentPurpose)
                .build();
        return tossPayment;
    }

    // 정상 반납 경우 - 결제 x
    public static TossPayment createTossPayment(PaymentResponseDTO tossResponse,
                                                 Order order) {
        return createTossPayment(tossResponse, order, null);
    }

    // Cancel 생성
    private static List<Cancel> createCancelPayment(CancelRequestDTO request,
                                                    PaymentResponseDTO response,
                                                    TossPayment tossPayment) {
        return response.getCancels().stream()
                .map(cancel -> Cancel.builder()
                        .paymentKey(request.getPaymentKey())
                        .cancelReason(request.getCancelReason())
                        .cancelAmount(cancel.getCancelAmount())
                        .cancelStatus(cancel.getCancelStatus())
                        .canceledAt(cancel.getCanceledAt())
                        .transactionKey(cancel.getTransactionKey())
                        .refundableAmount(cancel.getRefundableAmount())
                        .payment(tossPayment)
                        .build())
                .collect(Collectors.toList());
    }

    // 결제 조회
    public PaymentResponseDTO getPayment(String paymentKey) {
        TossPayment tossPayment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow(
                () -> new PaymentException(PAYMENT_NOT_FOUND));
        return PaymentResponseDTO.from(tossPayment);
    }

    // 연체료 결제내역 조회
    public PaymentResponseDTO getOverduePayment(Order order){
        TossPayment payment = paymentRepository.findByOrderAndPaymentPurpose(order, OVERDUE).orElseThrow(
                () -> new PaymentException(PAYMENT_NOT_FOUND));
        return PaymentResponseDTO.from(payment);
    }
}
