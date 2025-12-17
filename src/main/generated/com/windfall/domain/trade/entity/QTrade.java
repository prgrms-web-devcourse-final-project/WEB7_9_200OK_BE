package com.windfall.domain.trade.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrade is a Querydsl query type for Trade
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrade extends EntityPathBase<Trade> {

    private static final long serialVersionUID = 1910560271L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrade trade = new QTrade("trade");

    public final com.windfall.global.entity.QBaseEntity _super = new com.windfall.global.entity.QBaseEntity(this);

    //inherited
    public final BooleanPath activated = _super.activated;

    public final com.windfall.domain.auction.entity.QAuction auction;

    public final NumberPath<Long> buyerId = createNumber("buyerId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> finalPrice = createNumber("finalPrice", Long.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> sellerId = createNumber("sellerId", Long.class);

    public final EnumPath<com.windfall.domain.trade.enums.TradeStatus> status = createEnum("status", com.windfall.domain.trade.enums.TradeStatus.class);

    public QTrade(String variable) {
        this(Trade.class, forVariable(variable), INITS);
    }

    public QTrade(Path<? extends Trade> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrade(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrade(PathMetadata metadata, PathInits inits) {
        this(Trade.class, metadata, inits);
    }

    public QTrade(Class<? extends Trade> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.windfall.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

