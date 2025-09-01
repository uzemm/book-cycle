package com.uzem.book_cycle.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = -78366195L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    public final EnumPath<com.uzem.book_cycle.order.type.CancelReason> cancelReason = createEnum("cancelReason", com.uzem.book_cycle.order.type.CancelReason.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath deliveryMessage = createString("deliveryMessage");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.uzem.book_cycle.member.entity.QMember member;

    public final ListPath<OrderItem, QOrderItem> orderItems = this.<OrderItem, QOrderItem>createList("orderItems", OrderItem.class, QOrderItem.class, PathInits.DIRECT2);

    public final StringPath orderName = createString("orderName");

    public final StringPath orderNumber = createString("orderNumber");

    public final EnumPath<com.uzem.book_cycle.order.type.OrderStatus> orderStatus = createEnum("orderStatus", com.uzem.book_cycle.order.type.OrderStatus.class);

    public final EnumPath<com.uzem.book_cycle.payment.type.PaymentMethod> paymentMethod = createEnum("paymentMethod", com.uzem.book_cycle.payment.type.PaymentMethod.class);

    public final StringPath receiverAddress = createString("receiverAddress");

    public final StringPath receiverName = createString("receiverName");

    public final StringPath receiverPhone = createString("receiverPhone");

    public final StringPath receiverZipcode = createString("receiverZipcode");

    public final NumberPath<Long> rewardPoint = createNumber("rewardPoint", Long.class);

    public final NumberPath<Long> shippingFee = createNumber("shippingFee", Long.class);

    public final EnumPath<com.uzem.book_cycle.order.type.ShippingStatus> shippingStatus = createEnum("shippingStatus", com.uzem.book_cycle.order.type.ShippingStatus.class);

    public final StringPath tossOrderId = createString("tossOrderId");

    public final NumberPath<Long> totalPrice = createNumber("totalPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> usedPoint = createNumber("usedPoint", Long.class);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.uzem.book_cycle.member.entity.QMember(forProperty("member")) : null;
    }

}

