package com.uzem.book_cycle.order.service;

import com.uzem.book_cycle.admin.dto.rentals.RentalsBook;
import com.uzem.book_cycle.admin.dto.sales.SalesBook;
import com.uzem.book_cycle.admin.repository.RentalsRepository;
import com.uzem.book_cycle.admin.repository.SalesRepository;
import com.uzem.book_cycle.exception.MemberException;
import com.uzem.book_cycle.exception.OrderException;
import com.uzem.book_cycle.exception.RentalsException;
import com.uzem.book_cycle.exception.SalesException;
import com.uzem.book_cycle.member.entity.Member;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.order.dto.OrderItemRequestDTO;
import com.uzem.book_cycle.order.dto.OrderRequestDTO;
import com.uzem.book_cycle.order.dto.OrderResponseDTO;
import com.uzem.book_cycle.order.entity.Order;
import com.uzem.book_cycle.order.entity.OrderItem;
import com.uzem.book_cycle.order.repository.OrderRepository;
import com.uzem.book_cycle.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.uzem.book_cycle.admin.type.RentalsErrorCode.RENTALS_BOOK_NOT_FOUND;
import static com.uzem.book_cycle.admin.type.RentalsStatus.RENTED;
import static com.uzem.book_cycle.admin.type.SalesErrorCode.SALES_BOOK_NOT_FOUND;
import static com.uzem.book_cycle.admin.type.SalesStatus.SOLD;
import static com.uzem.book_cycle.member.type.MemberErrorCode.INSUFFICIENT_POINTS;
import static com.uzem.book_cycle.member.type.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.uzem.book_cycle.order.type.ItemType.SALE;
import static com.uzem.book_cycle.order.type.OrderErrorCode.INVALID_TOTAL_PRICE;
import static com.uzem.book_cycle.order.type.OrderErrorCode.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final SalesRepository salesRepository;
    private final RentalsRepository rentalsRepository;

    private static final double REWARD_POINT = 0.01;

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        // 1. 적립 포인트 계산
        long rewardPoint = calculateRewardPoint(requestDTO.getOrderItems());

        // 2. 로그인 회원 정보 조회
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Member member = memberRepository.findById(currentUserId).orElseThrow(
                () -> new MemberException(MEMBER_NOT_FOUND));

        // 3. 주문 엔티티 생성
        Order order = Order.from(requestDTO, new ArrayList<>(), member);

        // 4. 주문 항목 생성
        List<OrderItem> orderItems = getOrderItems(requestDTO, order);
        orderItems.forEach(order::addOrderItem);

        // 5. 총 결제 금액 계산
        long totalPrice = calculateTotalPrice(orderItems, order);
        if(totalPrice <= 0){
            throw new OrderException(INVALID_TOTAL_PRICE);
        }
        order.setTotalPrice(totalPrice);

        // 6. 사용 포인트 차감 및 적립포인트 추가
        long usedPoint = order.getUsedPoint();
        if (usedPoint > member.getPoint()) {
            throw new MemberException(INSUFFICIENT_POINTS);
        }
        member.setPoint(member.getPoint() + rewardPoint - usedPoint);
        order.setRewardPoint(rewardPoint);

        // 7. 판매, 대여 도서 상태 변경 및 저장
        updateOrderItems(orderItems);

        // 8. 주문 저장
        memberRepository.save(member); //  // 멤버 포인트 업데이트 반영
        orderRepository.save(order);

        // 9. 응답 DTO 변환 후 반환
        return OrderResponseDTO.from(order);
    }

    // 판매, 도서 상태 변경 및 저장
    public void updateOrderItems(List<OrderItem> orderItems) {
        orderItems.forEach(item -> {
            if(item.getItemType() == SALE) {
                item.getSalesBook().setSalesStatus(SOLD);
            } else {
                item.getRentalsBook().setRentalsStatus(RENTED);
            }
        });

        salesRepository.saveAll(orderItems.stream()
                .map(OrderItem::getSalesBook)
                        .filter(Objects::nonNull) // null 방지
                        .collect(Collectors.toList()));
        rentalsRepository.saveAll(orderItems.stream()
                .map(OrderItem::getRentalsBook)
                        .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    // 주문 도서
    private List<OrderItem> getOrderItems(OrderRequestDTO requestDTO, Order order) {
        List<OrderItem> orderItems = requestDTO.getOrderItems().stream() //OrderItem 생성
                .map(item -> {
                    if (item.getItemType() == SALE) {
                        SalesBook salesBook = salesRepository.findById(item.getBookId())
                                .orElseThrow(() -> new SalesException(SALES_BOOK_NOT_FOUND));
                        return OrderItem.from(item, order, salesBook, null);
                    } else {
                        RentalsBook rentalsBook = rentalsRepository.findById(item.getBookId())
                                .orElseThrow(() -> new RentalsException(RENTALS_BOOK_NOT_FOUND));
                        return OrderItem.from(item, order, null, rentalsBook);
                    }
                }).collect(Collectors.toList());
        return orderItems;
    }

    // 총금액 계산
    public long calculateTotalPrice(List<OrderItem> orderItems, Order order) {
        long totalItemPrice = orderItems.stream()
                .mapToLong(OrderItem::getItemPrice)
                .sum();
        long usedPoint = order.getUsedPoint();
        return totalItemPrice + order.getShippingFee() - usedPoint;
    }

    // 적립금 계산
    public long calculateRewardPoint(List<OrderItemRequestDTO> orderItems) {
       return orderItems.stream()
               .filter(item -> (item.getItemType() == SALE))
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
}
