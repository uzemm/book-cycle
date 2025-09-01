package com.uzem.book_cycle.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTossPayment is a Querydsl query type for TossPayment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTossPayment extends EntityPathBase<TossPayment> {

    private static final long serialVersionUID = 452063922L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTossPayment tossPayment = new QTossPayment("tossPayment");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> approvedAt = createDateTime("approvedAt", java.time.OffsetDateTime.class);

    public final ListPath<Cancel, QCancel> cancels = this.<Cancel, QCancel>createList("cancels", Cancel.class, QCancel.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.uzem.book_cycle.order.entity.QOrder order;

    public final StringPath orderName = createString("orderName");

    public final StringPath paymentKey = createString("paymentKey");

    public final EnumPath<com.uzem.book_cycle.payment.type.PaymentPurpose> paymentPurpose = createEnum("paymentPurpose", com.uzem.book_cycle.payment.type.PaymentPurpose.class);

    public final DateTimePath<java.time.OffsetDateTime> requestedAt = createDateTime("requestedAt", java.time.OffsetDateTime.class);

    public final EnumPath<com.uzem.book_cycle.payment.type.PaymentStatus> status = createEnum("status", com.uzem.book_cycle.payment.type.PaymentStatus.class);

    public final StringPath tossOrderId = createString("tossOrderId");

    public final EnumPath<com.uzem.book_cycle.payment.type.PaymentType> type = createEnum("type", com.uzem.book_cycle.payment.type.PaymentType.class);

    public QTossPayment(String variable) {
        this(TossPayment.class, forVariable(variable), INITS);
    }

    public QTossPayment(Path<? extends TossPayment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTossPayment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTossPayment(PathMetadata metadata, PathInits inits) {
        this(TossPayment.class, metadata, inits);
    }

    public QTossPayment(Class<? extends TossPayment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new com.uzem.book_cycle.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

