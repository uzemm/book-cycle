package com.uzem.book_cycle.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -546730405L;

    public static final QMember member = new QMember("member1");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final NumberPath<Integer> rentalCnt = createNumber("rentalCnt", Integer.class);

    public final ListPath<com.uzem.book_cycle.book.entity.RentalHistory, com.uzem.book_cycle.book.entity.QRentalHistory> rentalHistories = this.<com.uzem.book_cycle.book.entity.RentalHistory, com.uzem.book_cycle.book.entity.QRentalHistory>createList("rentalHistories", com.uzem.book_cycle.book.entity.RentalHistory.class, com.uzem.book_cycle.book.entity.QRentalHistory.class, PathInits.DIRECT2);

    public final EnumPath<com.uzem.book_cycle.member.type.Role> role = createEnum("role", com.uzem.book_cycle.member.type.Role.class);

    public final StringPath socialId = createString("socialId");

    public final EnumPath<com.uzem.book_cycle.member.type.SocialType> socialType = createEnum("socialType", com.uzem.book_cycle.member.type.SocialType.class);

    public final EnumPath<com.uzem.book_cycle.member.type.MemberStatus> status = createEnum("status", com.uzem.book_cycle.member.type.MemberStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

