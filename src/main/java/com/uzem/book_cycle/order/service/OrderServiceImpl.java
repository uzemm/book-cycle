package com.uzem.book_cycle.order.service;

import com.uzem.book_cycle.admin.entity.RentalBook;
import com.uzem.book_cycle.admin.entity.SalesBook;
import com.uzem.book_cycle.admin.repository.AdminRentalRepository;
import com.uzem.book_cycle.admin.repository.SalesRepository;
import com.uzem.book_cycle.book.repository.ReservationRepository;
import com.uzem.book_cycle.book.service.RentalServiceImpl;
import com.uzem.book_cycle.cart.entity.Cart;
import com.uzem.book_cycle.cart.repository.CartRepository;
import com.uzem.book_cycle.exception.*;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.entity.OrderItem;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.order.type.ItemType;
import com.uzem.book_cycle.payment.dto.PaymentRequestDTO;
import com.uzem.book_cycle.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.order.type.ItemType.RENTAL;
import static com.uzem.book_cycle.order.type.ItemType.SALE;
import static com.uzem.book_cycle.order.type.OrderErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final SalesRepository salesRepository;
    private final AdminRentalRepository rentalRepository;
    private final PaymentService paymentService;
    private final RentalServiceImpl rentalService;
    private final ReservationRepository reservationRepository;

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
        paymentService.processPayment(payment); // 결제 승인
        order.orderStatusCompleted(); // 주문 상태 변경

        // 판매, 대여 도서 상태 변경 및 저장
        List<OrderItem> orderItems = order.getOrderItems();
        updateOrderItems(orderItems, member, order, now);

        // 장바구니 주문 시 주문 완료 후 장바구니 삭제
        if(orderRequestDTO.isCartOrder()){
            cartRepository.deleteByIdInAndMember(orderRequestDTO.getCartIds(), member);
        }
        log.info("Deleted {} cart items for memberId {}",
                orderRequestDTO.getCartIds().size(), member.getId());



        // 응답 DTO 변환 후 반환
        return OrderResponseDTO.from(order);
    }

    @Transactional
    public Order createOrder(OrderRequestDTO requestDTO, Member member) {
        if(!requestDTO.isCartOrder()) {
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
        List<RentalBook> rentalBooks = orderItems.stream()
                .filter(item -> item.getItemType() == RENTAL)
                .map(OrderItem::getRentalBook)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        rentalBooks.forEach(rental -> {
            rental.rentalStatusRented(); // 대여상태 변경
            rentalService.createRentalHistory(rental, member, order, now); // 대여 이력 생성
            if(rental.getReservation() != null){
                reservationRepository.deleteByRentalBook(rental); // 확정 예약 삭제
            }
            member.rentalCnt(); // 대여 권수 ++
        });

        rentalRepository.saveAll(rentalBooks);
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
                .map(cart -> {
                    return createOrderItem(cart.getItemType(), cart.getBookId(), order, member);
                }).collect(Collectors.toList());
    }

    private List<OrderItem> getOrderItemsForDirectOrder(OrderRequestDTO requestDTO, Order order, Member member) {
        return requestDTO.getOrderItems().stream() //OrderItem 생성
                .map(item -> {
                    return createOrderItem(item.getItemType(), item.getBookId(), order, member);
                }).collect(Collectors.toList());
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
            validateRentalBookStatus(rentalBook, member);
            return OrderItem.fromRental(order, rentalBook);
        }
    }

    private static void validateSaleBookStatus(SalesBook saleBook) {
        if(saleBook.getSalesStatus() == SOLD){ // 판매완료
            throw new SalesException(ALREADY_SOLD_OUT_SALE_BOOK);
        }
    }
    private static void validateRentalBookStatus(RentalBook rentalBook, Member member) {
        if(rentalBook.getRentalStatus() == RENTED){ // 대여중
            throw new RentalException(ALREADY_RENTED);
        } else if(rentalBook.getRentalStatus() == OVERDUE){ // 연체중
            throw new RentalException(OVERDUE_RENTAL_BOOK);
        } else if(rentalBook.getRentalStatus() == PENDING_PAYMENT &&
        !rentalBook.getReservation().getMember().equals(member)){ // 결제 대기중
            throw new RentalException(PENDING_PAYMENT_RENTAL_BOOK);
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
    public OrderResponseDTO getOrders(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(ORDER_NOT_FOUND));
        return OrderResponseDTO.from(order);
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
