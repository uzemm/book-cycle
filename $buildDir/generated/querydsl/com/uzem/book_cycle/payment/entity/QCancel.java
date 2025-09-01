package com.uzem.book_cycle.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCancel is a Querydsl query type for Cancel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCancel extends EntityPathBase<Cancel> {

    private static final long serialVersionUID = 1834837715L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCancel cancel = new QCancel("cancel");

    public final NumberPath<Long> cancelAmount = createNumber("cancelAmount", Long.class);

    public final DateTimePath<java.time.OffsetDateTime> canceledAt = createDateTime("canceledAt", java.time.OffsetDateTime.class);

    public final StringPath cancelReason = createString("cancelReason");

    public final StringPath cancelStatus = createString("cancelStatus");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTossPayment payment;

    public final StringPath paymentKey = createString("paymentKey");

    public final NumberPath<Long> refundableAmount = createNumber("refundableAmount", Long.class);

    public final StringPath transactionKey = createString("transactionKey");

    public QCancel(String variable) {
        this(Cancel.class, forVariable(variable), INITS);
    }

    public QCancel(Path<? extends Cancel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCancel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCancel(PathMetadata metadata, PathInits inits) {
        this(Cancel.class, metadata, inits);
    }

    public QCancel(Class<? extends Cancel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payment = inits.isInitialized("payment") ? new QTossPayment(forProperty("payment"), inits.get("payment")) : null;
    }

}

