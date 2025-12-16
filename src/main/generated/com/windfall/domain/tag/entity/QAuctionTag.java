package com.windfall.domain.tag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionTag is a Querydsl query type for AuctionTag
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionTag extends EntityPathBase<AuctionTag> {

    private static final long serialVersionUID = 1571165762L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionTag auctionTag = new QAuctionTag("auctionTag");

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

    public final QTag tag;

    public QAuctionTag(String variable) {
        this(AuctionTag.class, forVariable(variable), INITS);
    }

    public QAuctionTag(Path<? extends AuctionTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionTag(PathMetadata metadata, PathInits inits) {
        this(AuctionTag.class, metadata, inits);
    }

    public QAuctionTag(Class<? extends AuctionTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.windfall.domain.auction.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

