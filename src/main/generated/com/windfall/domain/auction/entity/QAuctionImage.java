package com.windfall.domain.auction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionImage is a Querydsl query type for AuctionImage
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionImage extends EntityPathBase<AuctionImage> {

    private static final long serialVersionUID = -785748948L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionImage auctionImage = new QAuctionImage("auctionImage");

    public final com.windfall.global.entity.QBaseEntity _super = new com.windfall.global.entity.QBaseEntity(this);

    //inherited
    public final BooleanPath activated = _super.activated;

    public final QAuction auction;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath image = createString("image");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public QAuctionImage(String variable) {
        this(AuctionImage.class, forVariable(variable), INITS);
    }

    public QAuctionImage(Path<? extends AuctionImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionImage(PathMetadata metadata, PathInits inits) {
        this(AuctionImage.class, metadata, inits);
    }

    public QAuctionImage(Class<? extends AuctionImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

