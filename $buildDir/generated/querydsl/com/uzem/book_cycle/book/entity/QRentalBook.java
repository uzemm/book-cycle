package com.uzem.book_cycle.book.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRentalBook is a Querydsl query type for RentalBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRentalBook extends EntityPathBase<RentalBook> {

    private static final long serialVersionUID = 677194365L;

    public static final QRentalBook rentalBook = new QRentalBook("rentalBook");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    public final StringPath author = createString("author");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath image = createString("image");

    public final StringPath isbn = createString("isbn");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final StringPath link = createString("link");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath pubdate = createString("pubdate");

    public final StringPath publisher = createString("publisher");

    public final EnumPath<com.uzem.book_cycle.admin.type.RentalStatus> rentalStatus = createEnum("rentalStatus", com.uzem.book_cycle.admin.type.RentalStatus.class);

    public final ListPath<Reservation, QReservation> reservations = this.<Reservation, QReservation>createList("reservations", Reservation.class, QReservation.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QRentalBook(String variable) {
        super(RentalBook.class, forVariable(variable));
    }

    public QRentalBook(Path<? extends RentalBook> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRentalBook(PathMetadata metadata) {
        super(RentalBook.class, metadata);
    }

}

