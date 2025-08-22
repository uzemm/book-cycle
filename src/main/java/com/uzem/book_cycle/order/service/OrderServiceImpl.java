package com.uzem.book_cycle.order.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.admin.repository.AdminSalesRepository;
import com.uzem.book_cycle.book.entity.Reservation;
import com.uzem.book_cycle.book.service.RentalService;
import com.uzem.book_cycle.cart.entity.Cart;
import com.uzem.book_cycle.cart.repository.CartRepository;
import com.uzem.book_cycle.exception.*;
import com.uzem.book_cycle.order.dto.CancelOrderDTO;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.entity.OrderItem;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.order.type.CancelReason;
import com.uzem.book_cycle.order.type.ItemType;
import com.uzem.book_cycle.payment.dto.CancelPaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.dto.PaymentResponseDTO;
import com.uzem.book_cycle.payment.entity.TossPayment;
import com.uzem.book_cycle.payment.repository.PaymentRepository;
import com.uzem.book_cycle.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.admin.type.RentalErrorCode.*;
import static com.uzem.book_cycle.admin.type.RentalStatus.*;
import static com.uzem.book_cycle.admin.type.SalesErrorCode.*;
import static com.uzem.book_cycle.admin.type.SalesStatus.SOLD;
import static com.uzem.book_cycle.cart.type.CartErrorCode.CART_NOT_FOUND;
import static com.uzem.book_cycle.cart.type.CartErrorCode.RESERVATION_NOT_OWNED;
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.order.type.CancelReason.USER_REQUEST;
import static com.uzem.book_cycle.order.type.ItemType.RENTAL;
import static com.uzem.book_cycle.order.type.ItemType.SALE;
import static com.uzem.book_cycle.order.type.OrderErrorCode.*;
import static com.uzem.book_cycle.order.type.OrderStatus.PAID;
import static com.uzem.book_cycle.order.type.ShippingStatus.PREPARING;
import static com.uzem.book_cycle.payment.type.PaymentErrorCode.PAYMENT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final AdminSalesRepository salesRepository;
    private final AdminRentalRepository rentalRepository;
    private final PaymentService paymentService;
    private final RentalService rentalService;
    private final PaymentRepository paymentRepository;

    private static final double REWARD_POINT = 0.01;
    private final CartRepository cartRepository;

    // 주문 및 결제 완료
    @Transactional
    public OrderResponseDTO confirmOrder(OrderRequestDTO orderRequestDTO,
                                         PaymentRequestDTO payment,
                                         Long memberId, LocalDate now){
        // 회원 조회
        Member member = findByMemberId(memberId);

        // 주문 생성 및 저장
        Order order = createOrder(orderRequestDTO, member);
        PaymentResponseDTO paymentResponseDTO
                = paymentService.processPayment(payment);// 결제 승인
        order.orderStatusCompleted(); // 주문 상태 변경

        // 판매, 대여 도서 상태 변경 및 저장
        List<OrderItem> orderItems = order.getOrderItems();
        updateOrderItems(orderItems, member, order, now);

        // 장바구니 주문 시 주문 완료 후 장바구니 삭제
        if(orderRequestDTO.isCartOrder()){
            cartRepository.deleteByIdInAndMember(orderRequestDTO.getCartIds(), member);
        }
        //log.info("Deleted {} cart items for memberId {}",
        //        orderRequestDTO.getCartIds().size(), member.getId());

        // 응답 DTO 변환 후 반환
        return OrderResponseDTO.from(order, paymentResponseDTO);
    }

    @Transactional
    public Order createOrder(OrderRequestDTO requestDTO, Member member) {
        if(!requestDTO.isCartOrder() &&
                (requestDTO.getOrderItems() == null || requestDTO.getOrderItems().isEmpty())) {
            throw new OrderException(ORDER_ITEM_NOT_FOUND);
        }

        // 1. 주문 엔티티 생성
        Order order = Order.from(requestDTO, new ArrayList<>(), member);
        checkDuplicateOrder(order.getTossOrderId()); // 중복 주문 확인

        // 2. 주문 항목 생성
        List<OrderItem> orderItems = getOrderItems(requestDTO, order, member);
        orderItems.forEach(order::addOrderItem); // 양방향 연결
        order.setOrderName(createOrderName(orderItems)); // OrderName 설정

        // 3. 적립 포인트 계산
        long rewardPoint = calculateRewardPoint(orderItems);

        // 4. 총 결제 금액 계산
        long totalPrice = calculateTotalPrice(orderItems, order);
        order.setTotalPrice(totalPrice);

        // 5. 사용 포인트 차감 및 적립포인트 추가
        long usedPoint = order.getUsedPoint();
        member.usePoint(usedPoint);
        member.rewardPoint(rewardPoint);
        order.setRewardPoint(rewardPoint);

        // 6. 주문 저장
        memberRepository.save(member); // 멤버 포인트 업데이트 반영
        orderRepository.save(order);

        return order;
    }

    // 중복 주문 확인
    private void checkDuplicateOrder(String tossOrderId) {
        if(orderRepository.findByTossOrderId(tossOrderId).isPresent()) {
            throw new OrderException(DUPLICATE_ORDER);
        }
    }

    // 판매, 도서 상태 변경 및 저장
    @Transactional
    public void updateOrderItems(List<OrderItem> orderItems, Member member, Order order, LocalDate now) {
        updateSalesBookStatusToSold(orderItems);
        updateRentalBookStatusToRented(orderItems, member, order, now);
    }

    public void updateRentalBookStatusToRented(List<OrderItem> orderItems,
                                         Member member,
                                        Order order, LocalDate now) {
        // 대여도서 조회
        List<RentalBook> rentalBooks = orderItems.stream()
                .filter(item -> item.getItemType() == RENTAL)
                .map(OrderItem::getRentalBook)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        rentalBooks.forEach(rental -> {
            rental.rentalStatusRented(); // 대여중으로 상태 변경
            rentalService.createRentalHistory(rental, member, order, now); // 대여 이력 생성
            // 현재 예약자(1순위, 나) 비활성화
            rental.getReservations().stream()
                    .filter(reservation -> reservation.isActive()
                            && reservation.getReservationOrder() == 1
                            && reservation.getMember().getId().equals(member.getId()))
                    .findFirst()
                    .ifPresent(Reservation::cancelReservation);

            reorderReservations(rental); // 남은 예약자 순번 재정렬
            member.addRental(); // 대여 권수 ++
        });

        rentalRepository.saveAll(rentalBooks);
    }

    private static void reorderReservations(RentalBook rental) {
        int seq = 1;
        for(Reservation activeReservation : rental.getReservations()){
            if(activeReservation.isActive()){
                activeReservation.updateReservationOrder(seq++);
            }
        }
    }

    public void updateSalesBookStatusToSold(List<OrderItem> orderItems) {
        List<SalesBook> salesBooks = orderItems.stream()
                .filter(item -> item.getItemType() == SALE)
                .map(OrderItem::getSalesBook)
                .filter(Objects::nonNull)
                .toList();
        salesBooks.forEach(SalesBook::salesStatusSold);
        salesRepository.saveAll(salesBooks);
    }

    // 주문 도서
    private List<OrderItem> getOrderItems(OrderRequestDTO requestDTO, Order order, Member member) {
        if(requestDTO.isCartOrder()){
            // 장바구니 주문
            return getOrderItemsFromCart(requestDTO.getCartIds(), order, member);
        } else{
            // 바로 주문
            return getOrderItemsForDirectOrder(requestDTO, order, member);
        }
    }

    private List<OrderItem> getOrderItemsFromCart(List<Long> cartIds, Order order, Member member) {
        List<Cart> cartList = cartRepository.findAllByIdInAndMember(cartIds, member);
        if(cartList.isEmpty()){
            throw new CartException(CART_NOT_FOUND);
        }
        return cartList.stream()
                .map(cart -> createOrderItem(cart.getItemType(), cart.getBookId(), order, member)).collect(Collectors.toList());
    }

    private List<OrderItem> getOrderItemsForDirectOrder(
            OrderRequestDTO requestDTO, Order order, Member member) {
        return requestDTO.getOrderItems().stream() //OrderItem 생성
                .map(item -> createOrderItem(item.getItemType(),
                        item.getBookId(), order, member)).collect(Collectors.toList());
    }

    private OrderItem createOrderItem(ItemType type, Long bookId, Order order, Member member) {
        if (type == SALE) { // 판매 도서
            SalesBook salesBook = salesRepository.findById(bookId)
                    .orElseThrow(() -> new SalesException(SALES_BOOK_NOT_FOUND));
            // 도서 상태 검증
            validateSaleBookStatus(salesBook);
            return OrderItem.fromSales(order, salesBook);
        } else {
            // 대여 도서
            RentalBook rentalBook = rentalRepository.findById(bookId)
                    .orElseThrow(() -> new RentalException(RENTAL_BOOK_NOT_FOUND));
            // 도서 상태 검증
            validateRentalBookStatus(rentalBook);
            validateReservationOwned(rentalBook, member);
            return OrderItem.fromRental(order, rentalBook);
        }
    }

    private static void validateSaleBookStatus(SalesBook saleBook) {
        if(saleBook.getSalesStatus() == SOLD){ // 판매완료
            throw new SalesException(ALREADY_SOLD_OUT_SALE_BOOK);
        }
    }
    private static void validateRentalBookStatus(RentalBook rentalBook) {
        if(rentalBook.getRentalStatus() == RENTED){ // 대여중
            throw new RentalException(ALREADY_RENTED);
        } else if(rentalBook.getRentalStatus() == OVERDUE){ // 연체중
            throw new RentalException(OVERDUE_RENTAL_BOOK);
        }
    }

    private static void validateReservationOwned(RentalBook rentalBook, Member member) {
        if(rentalBook.getRentalStatus() == PENDING_PAYMENT){
            // 결제 대기 도서 내 예약인지 검증
            rentalBook.getReservations().stream()
                    .filter(reservation -> reservation.isActive()
                            && reservation.getReservationOrder() == 1
                            && reservation.getMember().getId().equals(member.getId()))
                    .findFirst()
                    .orElseThrow(() -> new CartException(RESERVATION_NOT_OWNED));
        }
    }

    // 총금액 계산
    public long calculateTotalPrice(List<OrderItem> orderItems, Order order) {
        long totalItemPrice = orderItems.stream()
                .mapToLong(OrderItem::getItemPrice)
                .sum();

        long usedPoint = order.getUsedPoint();
        long totalPrice = totalItemPrice + order.getShippingFee() - usedPoint;

        if(totalPrice <= 0){
            throw new OrderException(INVALID_TOTAL_PRICE);
        }

        return totalPrice;
    }

    // 적립금 계산
    public long calculateRewardPoint(List<OrderItem> orderItems) {
       return orderItems.stream()
               .mapToLong(item ->
                       (long) (item.getItemPrice() * REWARD_POINT)) // 1% 적립
               .sum(); // SALE 상품 여러 개일 경우 합산
    }

    // 주문 완료
    public OrderResponseDTO getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(ORDER_NOT_FOUND));
        TossPayment tossPayment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentException(PAYMENT_NOT_FOUND));
        PaymentResponseDTO paymentResponseDTO = PaymentResponseDTO.from(tossPayment);
        return OrderResponseDTO.from(order, paymentResponseDTO);
    }

    // 주문 취소
    @Transactional
    public CancelOrderDTO cancelMyOrder(Long memberId, Long orderId, CancelPaymentRequestDTO requestDTO) {
        // 1. 주문 조회 + 상태 검증 후 CANCEL_REQUESTED 저장
        Order order = markOrderCancelRequested(orderId);

        try{
            // 2. 결제 취소 시도 (외부 API, 트랜잭션 X)
            PaymentResponseDTO paymentResponseDTO =
                    paymentService.processCancelPayment(requestDTO);

            // 3. 결제 취소 성공 → 주문 최종 취소 (새 트랜잭션)
            cancelOrderWithRestoration(order, USER_REQUEST);
            return CancelOrderDTO.from(order, paymentResponseDTO);

        } catch (PaymentException  e){
            // 4. 결제 취소 실패 → 주문 상태 보류 (새 트랜잭션)
            orderCancelPending(order);
            log.error("Payment cancel failed. orderId={}, memberId={}, paymentKey={}, cause={}",
                    orderId, memberId, requestDTO.getPaymentKey(), e.getMessage());
            throw e;
        }
    }

    public void orderCancelPending(Order order) {
        order.cancelPending();
    }

    public Order markOrderCancelRequested(Long orderId) {
        Order order = orderRepository.findByIdAndShippingStatus(orderId, PREPARING)
                .orElseThrow(() -> new OrderException(ORDER_STATUS_SHIPPED));

        if(order.getOrderStatus() != PAID){
            throw new OrderException(ORDER_NOT_PAID);
        }
        order.cancelRequestOrder();
        return order;
    }

    public void cancelOrderWithRestoration(Order order, CancelReason reason) {
        order.cancelOrder(reason);
        restoreOrderStuff(order); // 포인트/대여권수 복원
        restoreBooks(order);
    }

    private void restoreOrderStuff(Order order) {
        Member member = order.getMember();

        int rentalCnt = (int) order.getOrderItems().stream()
                .filter(item -> item.getItemType() == RENTAL)
                .count();

        member.cancelOrder(order.getRewardPoint(), order.getUsedPoint(), rentalCnt);
    }

    public void restoreBooks(Order order) {
        for(OrderItem item : order.getOrderItems()){
            if(item.getItemType() == SALE){
                item.getSalesBook().cancelSalesStatusAvailable();
            } else{
                rentalService.restoreRentalBookStatus(item.getRentalBook());
                rentalService.restoreRentalHistory(order, item.getRentalBook());
            }
        }
    }

    // 회원 조회
    private Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));
    }

    public static String createOrderName(List<OrderItem> orderItems){
        if (orderItems == null || orderItems.isEmpty()) {
            return "도서 없음";
        }

        if(orderItems.size() == 1 ) {
            return orderItems.get(0).getTitle();
        } else {
            return orderItems.get(0).getTitle()+ " 외 " + (orderItems.size() - 1) + "권";
        }
    }
}
