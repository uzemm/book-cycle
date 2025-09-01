package com.uzem.book_cycle.admin.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uzem.book_cycle.admin.dto.member.AdminMemberDetailDTO;
import com.uzem.book_cycle.admin.dto.member.AdminMemberPreviewDTO;
import com.uzem.book_cycle.book.entity.QRentalHistory;
import com.uzem.book_cycle.member.entity.QMember;
import com.uzem.book_cycle.member.repository.MemberRepository;
import com.uzem.book_cycle.member.type.MemberStatus;
import com.uzem.book_cycle.order.entity.QOrder;
import com.uzem.book_cycle.reservation.entity.QReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.uzem.book_cycle.admin.type.RentalStatus.RETURNED;
import static com.uzem.book_cycle.order.type.OrderStatus.CANCELED;

@RequiredArgsConstructor
public class AdminMemberRepositoryImpl implements AdminMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;

    @Override
    public Page<AdminMemberPreviewDTO> searchMember(
            String name, String email, MemberStatus status, Pageable pageable) {
        QMember member = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        if (name != null) builder.and(member.name.containsIgnoreCase(name));
        if (email != null) builder.and(member.email.containsIgnoreCase(email));
        if (status != null) builder.and(member.status.eq(status));

        // Repository에서 제공하는 findAll 사용
        return memberRepository.findAll(builder, pageable)
                .map(m -> AdminMemberPreviewDTO.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .email(m.getEmail())
                        .status(m.getStatus())
                        .point(m.getPoint())
                        .rentalCnt(m.getRentalCnt())
                        .build()
                );
    }

    @Override
    public Optional<AdminMemberDetailDTO> getMemberDetail(Long memberId) {

        QMember member = QMember.member;
        QRentalHistory rentalHistory = QRentalHistory.rentalHistory;
        QReservation reservation = QReservation.reservation;
        QOrder order = QOrder.order;

        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(AdminMemberDetailDTO.class,
                                member.id,
                                member.name,
                                member.email,
                                member.point,
                                member.rentalCnt,

                                // 누적대여횟수
                                JPAExpressions.select(rentalHistory.count())
                                        .from(rentalHistory)
                                        .where(rentalHistory.member.eq(member)
                                                .and(rentalHistory.rentalStatus.eq(RETURNED))),
                                // 예약
                                JPAExpressions.select(reservation.count())
                                        .from(reservation)
                                        .where(reservation.member.eq(member)
                                                .and(reservation.isActive.isTrue())),
                                // 주문 횟수
                                JPAExpressions.select(order.count())
                                        .from(order)
                                        .where(order.member.eq(member)),
                                // 주문 취소 횟수
                                JPAExpressions.select(order.count())
                                        .from(order)
                                        .where(order.member.eq(member)
                                                .and(order.orderStatus.eq(CANCELED)))
                                ))
                        .from(member)
                        .where(member.id.eq(memberId))
                        .fetchOne()
        );
    }
}
