package com.uzem.book_cycle.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderItem is a Querydsl query type for OrderItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderItem extends EntityPathBase<OrderItem> {

    private static final long serialVersionUID = 1669421760L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderItem orderItem = new QOrderItem("orderItem");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Long> itemPrice = createNumber("itemPrice", Long.class);

    public final EnumPath<com.uzem.book_cycle.order.type.ItemType> itemType = createEnum("itemType", com.uzem.book_cycle.order.type.ItemType.class);

    public final QOrder order;

    public final com.uzem.book_cycle.book.entity.QRentalBook rentalBook;

    public final com.uzem.book_cycle.book.entity.QRentalHistory rentalHistory;

    public final com.uzem.book_cycle.book.entity.QSalesBook salesBook;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QOrderItem(String variable) {
        this(OrderItem.class, forVariable(variable), INITS);
    }

    public QOrderItem(Path<? extends OrderItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderItem(PathMetadata metadata, PathInits inits) {
        this(OrderItem.class, metadata, inits);
    }

    public QOrderItem(Class<? extends OrderItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
        this.rentalBook = inits.isInitialized("rentalBook") ? new com.uzem.book_cycle.book.entity.QRentalBook(forProperty("rentalBook")) : null;
        this.rentalHistory = inits.isInitialized("rentalHistory") ? new com.uzem.book_cycle.book.entity.QRentalHistory(forProperty("rentalHistory"), inits.get("rentalHistory")) : null;
        this.salesBook = inits.isInitialized("salesBook") ? new com.uzem.book_cycle.book.entity.QSalesBook(forProperty("salesBook")) : null;
    }

}

