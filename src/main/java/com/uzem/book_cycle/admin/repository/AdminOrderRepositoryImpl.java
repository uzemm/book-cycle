package com.uzem.book_cycle.admin.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uzem.book_cycle.admin.dto.order.AdminOrderDetailDTO;
import com.uzem.book_cycle.admin.dto.order.AdminOrderPreviewDTO;
import com.uzem.book_cycle.member.entity.QMember;
import com.uzem.book_cycle.order.dto.OrderItemResponseDTO;
import com.uzem.book_cycle.order.entity.QOrder;
import com.uzem.book_cycle.order.entity.QOrderItem;
import com.uzem.book_cycle.order.type.OrderStatus;
import com.uzem.book_cycle.order.type.ShippingStatus;
import com.uzem.book_cycle.rental.entity.QRentalBook;
import com.uzem.book_cycle.rental.entity.QRentalHistory;
import com.uzem.book_cycle.sales.entity.QSalesBook;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminOrderRepositoryImpl implements AdminOrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminOrderPreviewDTO> searchOrders(String memberName,
                                                   String email,
                                                   String orderNumber,
                                                   OrderStatus orderStatus,
                                                   ShippingStatus shippingStatus,
                                                   LocalDate startDate,
                                                   LocalDate endDate,
                                                   Pageable pageable) {

        QOrder order = QOrder.order;
        QMember member = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        if (memberName != null && !memberName.isEmpty()) builder.and(member.name.containsIgnoreCase(memberName));
        if (email != null && !email.isEmpty()) builder.and(member.email.containsIgnoreCase(email));
        if (orderNumber != null) builder.and(order.orderNumber.containsIgnoreCase(orderNumber));
        if (orderStatus != null) builder.and(order.orderStatus.eq(orderStatus));
        if (shippingStatus != null) builder.and(order.shippingStatus.eq(shippingStatus));
        if (startDate != null) builder.and(order.createdAt.goe(startDate.atStartOfDay()));
        if (endDate != null) builder.and(order.createdAt.loe(endDate.atTime(23, 59, 59)));

        // 메인 쿼리

        List<AdminOrderPreviewDTO> results = queryFactory
                .select(Projections.constructor(AdminOrderPreviewDTO.class,
                        order.id,
                        order.orderNumber,
                        member.name,
                        member.email,
                        order.orderStatus,
                        order.shippingStatus,
                        order.createdAt,
                        order.totalPrice
                ))
                .from(order)
                .join(order.member, member)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.createdAt.desc())
                .fetch();

        // count 쿼리
        Long count = queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .where(builder)
                .fetchOne();

        return new PageImpl<AdminOrderPreviewDTO>(results, pageable, count != null ? count : 0);
    }

    @Override
    public Optional<AdminOrderDetailDTO> findOrderDetail(Long orderId) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        AdminOrderDetailDTO orderDetail = queryFactory.
                select(Projections.constructor(AdminOrderDetailDTO.class,
                        order.id,
                        order.orderNumber,
                        member.name,
                        member.email,
                        member.address,
                        member.phone,
                        order.orderStatus,
                        order.shippingStatus,
                        order.createdAt,
                        order.totalPrice

                ))
                .from(order)
                .join(order.member, member)
                .where(order.id.eq(orderId))
                .fetchOne();


        return Optional.ofNullable(orderDetail);
    }

    @Override
    public List<OrderItemResponseDTO> findOrderItems(Long orderId) {

        QOrderItem item = QOrderItem.orderItem;
        QSalesBook salesBook = QSalesBook.salesBook;
        QRentalBook rentalBook = QRentalBook.rentalBook;
        QRentalHistory rentalHistory = QRentalHistory.rentalHistory;
        return queryFactory.
                select(Projections.constructor(OrderItemResponseDTO.class,
                        salesBook.id,
                        rentalBook.id,
                        item.itemType,
                        item.itemPrice,
                        item.salesBook.title.coalesce(rentalBook.title),
                        rentalHistory.rentalDate,
                        rentalHistory.returnDate,
                        rentalHistory.actualReturnDate,
                        rentalHistory.isOverduePayment
                        ))
                .from(item)
                .leftJoin(item.salesBook, salesBook)
                .leftJoin(item.rentalBook, rentalBook)
                .leftJoin(rentalHistory).on(rentalHistory.orderItem.eq(item))
                .where(item.order.id.eq(orderId))
                .fetch();
    }


}
