package com.windfall.domain.recentview.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecentView is a Querydsl query type for RecentView
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecentView extends EntityPathBase<RecentView> {

    private static final long serialVersionUID = 1173911305L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecentView recentView = new QRecentView("recentView");

    public final com.windfall.global.entity.QBaseEntity _super = new com.windfall.global.entity.QBaseEntity(this);

    //inherited
    public final BooleanPath activated = _super.activated;

    public final com.windfall.domain.auction.entity.QAuction auction;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRecentView(String variable) {
        this(RecentView.class, forVariable(variable), INITS);
    }

    public QRecentView(Path<? extends RecentView> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecentView(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecentView(PathMetadata metadata, PathInits inits) {
        this(RecentView.class, metadata, inits);
    }

    public QRecentView(Class<? extends RecentView> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.windfall.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

