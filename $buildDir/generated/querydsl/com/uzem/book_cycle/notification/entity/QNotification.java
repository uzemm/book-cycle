package com.uzem.book_cycle.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 2010510397L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isRead = createBoolean("isRead");

    public final com.uzem.book_cycle.member.entity.QMember member;

    public final StringPath message = createString("message");

    public final com.uzem.book_cycle.order.entity.QOrder order;

    public final NumberPath<Integer> overdueDay = createNumber("overdueDay", Integer.class);

    public final com.uzem.book_cycle.book.entity.QRentalBook rentalBook;

    public final EnumPath<com.uzem.book_cycle.notification.type.NotificationType> type = createEnum("type", com.uzem.book_cycle.notification.type.NotificationType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.uzem.book_cycle.member.entity.QMember(forProperty("member")) : null;
        this.order = inits.isInitialized("order") ? new com.uzem.book_cycle.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
        this.rentalBook = inits.isInitialized("rentalBook") ? new com.uzem.book_cycle.book.entity.QRentalBook(forProperty("rentalBook")) : null;
    }

}

