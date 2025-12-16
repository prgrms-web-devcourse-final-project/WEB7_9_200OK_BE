package com.windfall.domain.like.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionLike is a Querydsl query type for AuctionLike
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionLike extends EntityPathBase<AuctionLike> {

    private static final long serialVersionUID = 1959629754L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionLike auctionLike = new QAuctionLike("auctionLike");

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

    public QAuctionLike(String variable) {
        this(AuctionLike.class, forVariable(variable), INITS);
    }

    public QAuctionLike(Path<? extends AuctionLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionLike(PathMetadata metadata, PathInits inits) {
        this(AuctionLike.class, metadata, inits);
    }

    public QAuctionLike(Class<? extends AuctionLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.windfall.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

