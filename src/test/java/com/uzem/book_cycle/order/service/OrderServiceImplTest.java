package com.uzem.book_cycle.order.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.admin.repository.SalesRepository;
import com.uzem.book_cycle.book.service.RentalServiceImpl;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.dto.OrderItemRequestDTO;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.entity.OrderItem;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.order.type.ItemType;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.RENTED;
import static com.uzem.book_cycle.admin.type.SalesStatus.SOLD;
import static com.uzem.book_cycle.order.type.ItemType.RENTAL;
import static com.uzem.book_cycle.order.type.ItemType.SALE;
import static com.uzem.book_cycle.order.type.OrderStatus.COMPLETED;
import static com.uzem.book_cycle.order.type.OrderStatus.PAID_READY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SalesRepository salesRepository;

    @Mock
    private AdminRentalRepository rentalRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RentalServiceImpl rentalService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("주문 생성")
    void createOrder() throws Exception {
        //given
        Member member = createMember();
        OrderRequestDTO request = getOrderRequestDTO();
        SalesBook salesBook = SalesBook.builder()
                .id(1L)
                .title("판매용 도서")
                .price(5000L)
                .build();

        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .build();

        given(salesRepository.findById(1L)).willReturn(Optional.of(salesBook));
        given(rentalRepository.findById(1L)).willReturn(Optional.of(rentalBook));
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        //when
        Order order = orderService.createOrder(request, member);

        //then
        assertThat(order).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(9400L);
        assertThat(order.getOrderItems().size()).isEqualTo(2);
        assertThat(order.getRewardPoint()).isEqualTo(50L);
        assertThat(order.getUsedPoint()).isEqualTo(100L);
        assertThat(order.getOrderStatus()).isEqualTo(PAID_READY);
        assertThat(member.getPoint()).isEqualTo(150L);

        verify(memberRepository, times(1)).save(member);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("주문 및 결제 성공")
    void confirmOrder() throws Exception {
        //given
        PaymentRequestDTO payment = getPaymentRequestDTO();
        Member member = createMember();
        OrderRequestDTO request = getOrderRequestDTO();
        Order order = Order.builder().build(); // dummy order
        SalesBook salesBook = SalesBook.builder()
                .id(1L)
                .title("판매용 도서")
                .price(5000L)
                .build();

        RentalBook rentalBook = RentalBook.builder()
                .id(1L)
                .title("대여용 도서")
                .price(1000L)
                .build();

        List<OrderItem> orderItems = getOrderItems(order, salesBook, rentalBook);
        orderItems.forEach(order::addOrderItem);

        given(salesRepository.findById(1L)).willReturn(Optional.of(salesBook));
        given(rentalRepository.findById(1L)).willReturn(Optional.of(rentalBook));

        //when
        OrderResponseDTO result = orderService.confirmOrder(
                request, payment, member, LocalDate.now());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getOrderName()).isEqualTo("판매용 도서 외 1권");
        assertThat(result.getStatus()).isEqualTo(COMPLETED);
        assertThat(result.getTotalPrice()).isEqualTo(9400L);
        assertThat(salesBook.getSalesStatus()).isEqualTo(SOLD);
        assertThat(rentalBook.getRentalStatus()).isEqualTo(RENTED);
        assertThat(member.getRentalCnt()).isEqualTo(1);

        verify(paymentService, times(1)).processPayment(payment);
        verify(rentalService, times(1)).createRentalHistory(
                eq(rentalBook),
                eq(member),
                any(Order.class),
                any(LocalDate.class)
        );

    }

    private static List<OrderItem> getOrderItems(Order order, SalesBook salesBook, RentalBook rentalBook) {
        List<OrderItem> items = new ArrayList<>();

        OrderItem saleItem = OrderItem.from(
                new OrderItemRequestDTO(salesBook.getId(), SALE, salesBook.getPrice()),
                order,
                salesBook,
                null
        );

        OrderItem rentalItem = OrderItem.from(
                new OrderItemRequestDTO(rentalBook.getId(), ItemType.RENTAL, rentalBook.getPrice()),
                order,
                null,
                rentalBook
        );

        items.add(saleItem);
        items.add(rentalItem);

        return items;
    }


    private static OrderRequestDTO getOrderRequestDTO() {
        List<OrderItemRequestDTO> item = getOrderItemRequestDTO();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .receiverZipcode("123456")
                .receiverAddress("null")
                .receiverPhone("01011111111")
                .receiverName("test")
                .usedPoint(100L)
                .orderItems(item)
                .build();
        return request;
    }

    private static List<OrderItemRequestDTO> getOrderItemRequestDTO() {
        List<OrderItemRequestDTO> item = new ArrayList<>();
        item.add(new OrderItemRequestDTO(1L, SALE, 5000L));
        item.add(new OrderItemRequestDTO(1L, RENTAL, 1000L));
        return item;
    }

    private static Member createMember() {
        Member member = Member.builder()
                .id(1L)
                .email("test@uzem.com")
                .point(200L)
                .build();
        return member;
    }

    private static PaymentRequestDTO getPaymentRequestDTO() {
        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .build();
        return payment;
    }
}