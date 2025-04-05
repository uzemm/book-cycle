package com.uzem.book_cycle.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.uzem.book_cycle.payment.type.PaymentMethod.CARD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 및 결제 성공")
    @WithMockUser(username = "test@uzem.com", roles = {"USER"})
    void createOrder() throws Exception {
        //given
        OrderRequestDTO request = OrderRequestDTO.builder()
                .receiverZipcode("123456")
                .receiverAddress("null")
                .receiverPhone("01011111111")
                .receiverName("test")
                .paymentMethod(CARD)
                .build();

        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder()
                .totalPrice(9400L)
                .tossOrderId("generateTossOrderId")
                .build();

        //when
        when(orderService.confirmOrder(any(), any(), any(), any())).thenReturn(orderResponseDTO);

        //then
        mockMvc.perform(post("/orders")
                .with(csrf())
                .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tossOrderId").value("generateTossOrderId"))
                .andExpect(jsonPath("$.totalPrice").value(9400L));
    }
}