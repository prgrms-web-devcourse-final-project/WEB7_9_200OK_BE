package com.windfall.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuction is a Querydsl query type for Auction
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuction extends EntityPathBase<Auction> {

    private static final long serialVersionUID = 1670609519L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuction auction = new QAuction("auction");

    public final com.windfall.global.entity.QBaseEntity _super = new com.windfall.global.entity.QBaseEntity(this);

    //inherited
    public final BooleanPath activated = _super.activated;

    public final EnumPath<com.windfall.domain.auction.enums.AuctionCategory> category = createEnum("category", com.windfall.domain.auction.enums.AuctionCategory.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> currentPrice = createNumber("currentPrice", Long.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> dropAmount = createNumber("dropAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> endedAt = createDateTime("endedAt", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final com.windfall.domain.user.entity.QUser seller;

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> startPrice = createNumber("startPrice", Long.class);

    public final EnumPath<com.windfall.domain.auction.enums.AuctionStatus> status = createEnum("status", com.windfall.domain.auction.enums.AuctionStatus.class);

    public final NumberPath<Long> stopLoss = createNumber("stopLoss", Long.class);

    public final StringPath title = createString("title");

    public QAuction(String variable) {
        this(Auction.class, forVariable(variable), INITS);
    }

    public QAuction(Path<? extends Auction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuction(PathMetadata metadata, PathInits inits) {
        this(Auction.class, metadata, inits);
    }

    public QAuction(Class<? extends Auction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.seller = inits.isInitialized("seller") ? new com.windfall.domain.user.entity.QUser(forProperty("seller")) : null;
    }

}

