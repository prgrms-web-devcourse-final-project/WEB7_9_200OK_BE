package com.windfall.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionPriceHistory is a Querydsl query type for AuctionPriceHistory
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionPriceHistory extends EntityPathBase<AuctionPriceHistory> {

    private static final long serialVersionUID = 1329403674L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionPriceHistory auctionPriceHistory = new QAuctionPriceHistory("auctionPriceHistory");

    public final com.windfall.global.entity.QBaseEntity _super = new com.windfall.global.entity.QBaseEntity(this);

    //inherited
    public final BooleanPath activated = _super.activated;

    public final QAuction auction;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final NumberPath<Long> viewerCount = createNumber("viewerCount", Long.class);

    public QAuctionPriceHistory(String variable) {
        this(AuctionPriceHistory.class, forVariable(variable), INITS);
    }

    public QAuctionPriceHistory(Path<? extends AuctionPriceHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionPriceHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionPriceHistory(PathMetadata metadata, PathInits inits) {
        this(AuctionPriceHistory.class, metadata, inits);
    }

    public QAuctionPriceHistory(Class<? extends AuctionPriceHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

