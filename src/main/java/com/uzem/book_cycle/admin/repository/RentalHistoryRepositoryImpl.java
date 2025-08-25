package com.uzem.book_cycle.admin.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uzem.book_cycle.admin.dto.rental.AdminRentalStatusDTO;
import com.uzem.book_cycle.admin.type.RentalStatus;
import com.uzem.book_cycle.book.entity.QRentalBook;
import com.uzem.book_cycle.book.entity.QRentalHistory;
import com.uzem.book_cycle.member.entity.QMember;
import com.uzem.book_cycle.order.entity.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class RentalHistoryRepositoryImpl implements RentalHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<AdminRentalStatusDTO> searchRentals(Long memberId,
                                                        RentalStatus rentalStatus,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        Pageable pageable) {
        QRentalHistory rh = QRentalHistory.rentalHistory;
        QMember m = QMember.member;
        QRentalBook rb = QRentalBook.rentalBook;
        QOrder o = QOrder.order;

        BooleanBuilder builder = new BooleanBuilder();
        if(memberId != null) builder.and(m.id.eq(memberId));
        if(rentalStatus != null) builder.and(rh.rentalStatus.eq(rentalStatus));
        if(startDate != null) builder.and(rh.rentalDate.goe(startDate));
        if(endDate != null) builder.and(rh.rentalDate.loe(endDate));

        // content 조회
        List<AdminRentalStatusDTO> content = queryFactory.select(Projections.constructor(AdminRentalStatusDTO.class,
                        m.id,
                        m.name,
                        rb.title,
                        rh.rentalDate,
                        rh.returnDate,
                        rh.actualReturnDate,
                        rh.overdueFee,
                        rb.rentalStatus,
                        rh.isOverduePayment,
                        o.orderNumber
                ))
                .from(rh)
                .join(rh.member, m)
                .join(rh.rentalBook, rb)
                .join(rh.order, o)
                .where(builder)
                .orderBy(rh.rentalDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리
        Long total = queryFactory
                .select(rh.count())
                .from(rh)
                .join(rh.member, m)
                .join(rh.rentalBook, rb)
                .join(rh.order, o)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total: 0L);
    }
}
