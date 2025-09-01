package com.uzem.book_cycle.admin.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminLog is a Querydsl query type for AdminLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminLog extends EntityPathBase<AdminLog> {

    private static final long serialVersionUID = -1700697001L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminLog adminLog = new QAdminLog("adminLog");

    public final com.uzem.book_cycle.entity.QBaseEntity _super = new com.uzem.book_cycle.entity.QBaseEntity(this);

    public final EnumPath<com.uzem.book_cycle.admin.type.LogActionType> action = createEnum("action", com.uzem.book_cycle.admin.type.LogActionType.class);

    public final StringPath adminName = createString("adminName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.uzem.book_cycle.member.entity.QMember member;

    public final StringPath reason = createString("reason");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAdminLog(String variable) {
        this(AdminLog.class, forVariable(variable), INITS);
    }

    public QAdminLog(Path<? extends AdminLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminLog(PathMetadata metadata, PathInits inits) {
        this(AdminLog.class, metadata, inits);
    }

    public QAdminLog(Class<? extends AdminLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.uzem.book_cycle.member.entity.QMember(forProperty("member")) : null;
    }

}

