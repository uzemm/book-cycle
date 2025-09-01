package com.uzem.book_cycle.auth.email.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmailVerification is a Querydsl query type for EmailVerification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailVerification extends EntityPathBase<EmailVerification> {

    private static final long serialVersionUID = 954210490L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmailVerification emailVerification = new QEmailVerification("emailVerification");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expiresAt = createDateTime("expiresAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.uzem.book_cycle.member.entity.QMember member;

    public final StringPath newEmail = createString("newEmail");

    public final StringPath verificationCode = createString("verificationCode");

    public final BooleanPath verified = createBoolean("verified");

    public QEmailVerification(String variable) {
        this(EmailVerification.class, forVariable(variable), INITS);
    }

    public QEmailVerification(Path<? extends EmailVerification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmailVerification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmailVerification(PathMetadata metadata, PathInits inits) {
        this(EmailVerification.class, metadata, inits);
    }

    public QEmailVerification(Class<? extends EmailVerification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.uzem.book_cycle.member.entity.QMember(forProperty("member")) : null;
    }

}

