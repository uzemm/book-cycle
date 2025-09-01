package com.uzem.book_cycle.book.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRentalHistory is a Querydsl query type for RentalHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRentalHistory extends EntityPathBase<RentalHistory> {

    private static final long serialVersionUID = 1698290816L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRentalHistory rentalHistory = new QRentalHistory("rentalHistory");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    public final DatePath<java.time.LocalDate> actualReturnDate = createDate("actualReturnDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> canceledAt = createDateTime("canceledAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isOverduePayment = createBoolean("isOverduePayment");

    public final com.uzem.book_cycle.member.entity.QMember member;

    public final com.uzem.book_cycle.order.entity.QOrder order;

    public final com.uzem.book_cycle.order.entity.QOrderItem orderItem;

    public final NumberPath<Long> overdueFee = createNumber("overdueFee", Long.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final QRentalBook rentalBook;

    public final DatePath<java.time.LocalDate> rentalDate = createDate("rentalDate", java.time.LocalDate.class);

    public final EnumPath<com.uzem.book_cycle.admin.type.RentalStatus> rentalStatus = createEnum("rentalStatus", com.uzem.book_cycle.admin.type.RentalStatus.class);

    public final DatePath<java.time.LocalDate> returnDate = createDate("returnDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRentalHistory(String variable) {
        this(RentalHistory.class, forVariable(variable), INITS);
    }

    public QRentalHistory(Path<? extends RentalHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRentalHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRentalHistory(PathMetadata metadata, PathInits inits) {
        this(RentalHistory.class, metadata, inits);
    }

    public QRentalHistory(Class<? extends RentalHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.uzem.book_cycle.member.entity.QMember(forProperty("member")) : null;
        this.order = inits.isInitialized("order") ? new com.uzem.book_cycle.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
        this.orderItem = inits.isInitialized("orderItem") ? new com.uzem.book_cycle.order.entity.QOrderItem(forProperty("orderItem"), inits.get("orderItem")) : null;
        this.rentalBook = inits.isInitialized("rentalBook") ? new QRentalBook(forProperty("rentalBook")) : null;
    }

}

