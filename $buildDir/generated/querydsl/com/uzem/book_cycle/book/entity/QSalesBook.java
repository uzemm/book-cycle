package com.uzem.book_cycle.book.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSalesBook is a Querydsl query type for SalesBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSalesBook extends EntityPathBase<SalesBook> {

    private static final long serialVersionUID = -277735483L;

    public static final QSalesBook salesBook = new QSalesBook("salesBook");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    public final StringPath author = createString("author");

    public final EnumPath<com.uzem.book_cycle.admin.type.BookQuality> bookQuality = createEnum("bookQuality", com.uzem.book_cycle.admin.type.BookQuality.class);

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

    public final EnumPath<com.uzem.book_cycle.admin.type.SalesStatus> salesStatus = createEnum("salesStatus", com.uzem.book_cycle.admin.type.SalesStatus.class);

    public final DateTimePath<java.time.LocalDateTime> soldAt = createDateTime("soldAt", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSalesBook(String variable) {
        super(SalesBook.class, forVariable(variable));
    }

    public QSalesBook(Path<? extends SalesBook> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSalesBook(PathMetadata metadata) {
        super(SalesBook.class, metadata);
    }

}

