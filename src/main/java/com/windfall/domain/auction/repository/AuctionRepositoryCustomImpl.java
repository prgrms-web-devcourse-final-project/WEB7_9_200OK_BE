package com.windfall.domain.auction.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.windfall.api.auction.dto.response.AuctionSearchResponse;
import com.windfall.api.auction.dto.response.info.PopularInfo;
import com.windfall.api.auction.dto.response.info.ProcessInfo;
import com.windfall.api.auction.dto.response.info.ScheduledInfo;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static com.windfall.domain.auction.entity.QAuction.auction;
import static com.windfall.domain.auction.entity.QAuctionImage.auctionImage;
import static com.windfall.domain.auction.entity.QAuctionPriceHistory.auctionPriceHistory;

@RequiredArgsConstructor
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ProcessInfo> getProcessInfo(AuctionStatus status, int limit){
    return queryFactory
            .select(Projections.constructor(ProcessInfo.class,
                auction.id,
                JPAExpressions
                    .select(auctionImage.image)
                    .from(auctionImage)
                    .where(auctionImage.auction.id.eq(auction.id))
                    .orderBy(auctionImage.id.asc())
                    .limit(1),
                auction.title,
                auction.startPrice,
                auction.currentPrice,
                calculateDiscountRate(),
                Expressions.constant(false),
                auction.startedAt
            ))
            .from(auction)
            .where(auction.status.eq(status))
            .orderBy(auction.startedAt.asc())
            .limit(limit)
            .fetch();
  }

  @Override
  public List<ScheduledInfo> getScheduledInfo(AuctionStatus status, int limit){
    return queryFactory
        .select(Projections.constructor(ScheduledInfo.class,
            auction.id,
            JPAExpressions
                .select(auctionImage.image)
                .from(auctionImage)
                .where(auctionImage.auction.id.eq(auction.id))
                .orderBy(auctionImage.id.asc())
                .limit(1),
            auction.title,
            auction.startPrice,
            Expressions.constant(false),
            auction.startedAt
        ))
        .from(auction)
        .where(auction.status.eq(status))
        .orderBy(auction.startedAt.asc())
        .limit(limit)
        .fetch();
  }

  @Override
  public List<PopularInfo> getPopularInfo(AuctionStatus status, int limit){
    return queryFactory
        .select(Projections.constructor(PopularInfo.class,
            auction.id,
            JPAExpressions
                .select(auctionImage.image)
                .from(auctionImage)
                .where(auctionImage.auction.id.eq(auction.id))
                .orderBy(auctionImage.id.asc())
                .limit(1),
            auction.title,
            auction.startPrice,
            auction.currentPrice,
            calculateDiscountRate(),
            Expressions.constant(false),
            auction.startedAt
        ))
        .from(auction)
        .where(auction.status.eq(status))
        .leftJoin(auctionPriceHistory)
        .on(auction.id.eq(auctionPriceHistory.auction.id))
        .orderBy(auctionPriceHistory.viewerCount.desc())
        .limit(limit)
        .fetch();
  }

  @Override
  public Slice<AuctionSearchResponse> searchAuction(Pageable pageable,String query, AuctionCategory category, AuctionStatus status, Long maxPrice, Long maxPrice1){
    int pageSize = pageable.getPageSize();
    boolean hasNext = false;
    List<AuctionSearchResponse> auctionList = queryFactory.select(
            Projections.constructor(AuctionSearchResponse.class,
                auction.id,
                JPAExpressions
                    .select(auctionImage.image)
                    .from(auctionImage)
                    .where(auctionImage.auction.id.eq(auction.id))
                    .orderBy(auctionImage.id.asc())
                    .limit(1),
                auction.title,
                auction.startPrice,
                auction.currentPrice,
                calculateDiscountRate(),
                Expressions.constant(false),
                auction.startedAt,
                auction.status
            ))
        .from(auction)
        .where(auction.status.eq(status).and(
            auction.title.contains(query).or(
                auction.description.contains(query)
            )
        ))
        .leftJoin(auctionPriceHistory)
        .on(auction.id.eq(auctionPriceHistory.auction.id))
        .orderBy(auctionPriceHistory.viewerCount.desc())
        .fetch();

    if(auctionList.size() > pageSize){
      auctionList.remove(pageSize);
      hasNext = true;
    }
    return new SliceImpl<>(auctionList,pageable,hasNext);
  }

  private NumberExpression<Long> calculateDiscountRate() {
    return Expressions.numberTemplate(
        Long.class,
        "100 - ({0} * 100.0 / {1})",
        auction.currentPrice,
        auction.startPrice
    );
  }
}
