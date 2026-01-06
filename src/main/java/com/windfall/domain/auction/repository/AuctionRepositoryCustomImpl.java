package com.windfall.domain.auction.repository;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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
import com.windfall.domain.notification.enums.NotificationSettingType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;


import static com.windfall.domain.auction.entity.QAuction.auction;
import static com.windfall.domain.tag.entity.QAuctionTag.auctionTag;
import static com.windfall.domain.auction.entity.QAuctionImage.auctionImage;
import static com.windfall.domain.auction.entity.QAuctionPriceHistory.auctionPriceHistory;
import static com.windfall.domain.notification.entity.QNotificationSetting.notificationSetting;

@RequiredArgsConstructor
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ProcessInfo> getProcessInfo(AuctionStatus status, int limit) {
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
  public List<ScheduledInfo> getScheduledInfo(AuctionStatus status,Long userId, int limit) {
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
            auction.startedAt,
            isAuctionStartNotificationEnabled(userId)
        ))
        .from(auction)
        .leftJoin(notificationSetting)
        .on(auctionStartNotificationCondition(userId))
        .where(auction.status.eq(status))
        .orderBy(auction.startedAt.asc())
        .limit(limit)
        .fetch();
  }

  @Override
  public List<PopularInfo> getPopularInfo(AuctionStatus status, int limit) {
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
        .orderBy(maxViewerCount().desc())
        .limit(limit)
        .fetch();
  }

  @Override
  public Slice<AuctionSearchResponse> searchAuction(Pageable pageable, String query,
      AuctionCategory category, AuctionStatus status, Long minPrice, Long maxPrice, List<Long> tagIds,Long userId) {
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
                auction.status,
                isAuctionStartNotificationEnabled(userId)
            ))
        .from(auction)
        .leftJoin(notificationSetting)
        .on(auctionStartNotificationCondition(userId))
        .where(
            containsQuery(query),
            eqCategory(category),
            eqStatus(status),
            priceBetween(minPrice, maxPrice),
            containsAllTagIds(tagIds)
        )
        .orderBy(
            statusOrder(status),
            auctionSort(pageable)
        )
        .offset(pageable.getOffset() - pageSize)
        .limit(pageSize + 1)
        .fetch();

    if (auctionList.size() > pageSize) {
      auctionList.remove(pageSize);
      hasNext = true;
    }
    return new SliceImpl<>(auctionList, pageable, hasNext);
  }

  private BooleanExpression containsAllTagIds(List<Long> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) {
      return null;
    }

    return auction.id.in(
        JPAExpressions
            .select(auctionTag.auction.id)
            .from(auctionTag)
            .where(auctionTag.tag.id.in(tagIds))
            .groupBy(auctionTag.auction.id)
    );
  }

  private OrderSpecifier<?> statusOrder(AuctionStatus status) {
    if (status != null) {
      return auction.startedAt.asc();
    }
    NumberExpression<Integer> order = new CaseBuilder()
        .when(auction.status.eq(AuctionStatus.PROCESS)).then(1)
        .when(auction.status.eq(AuctionStatus.SCHEDULED)).then(2)
        .when(auction.status.eq(AuctionStatus.COMPLETED)).then(3)
        .otherwise(4);

    return order.asc();
  }

  private NumberExpression<Long> calculateDiscountRate() {
    return Expressions.numberTemplate(
        Long.class,
        "100 - ({0} * 100.0 / {1})",
        auction.currentPrice,
        auction.startPrice
    );
  }

  private OrderSpecifier<?> auctionSort(Pageable page) {
    Sort.Order order = page.getSort().stream().findFirst().orElse(null);

    if (order != null) {
      Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

      switch (order.getProperty()) {
        case "createDate":
          return new OrderSpecifier<>(direction, auction.createDate);
        case "viewCount":
          return new OrderSpecifier<>(direction, maxViewerCount());
        case "startedAt":
          return new OrderSpecifier<>(direction, auction.startedAt);
      }
    }

    return null;
  }

private NumberExpression<Long> maxViewerCount() {
  return Expressions.numberTemplate(
      Long.class,
      "({0})",
      JPAExpressions
          .select(auctionPriceHistory.viewerCount.max())
          .from(auctionPriceHistory)
          .where(auctionPriceHistory.auction.id.eq(auction.id))
  );
}

private BooleanExpression containsQuery(String query) {
  return auction.title.containsIgnoreCase(query)
      .or(auction.description.containsIgnoreCase(query));
}

private BooleanExpression eqCategory(AuctionCategory category) {
  if (category == null) {
    return null;
  }
  return auction.category.eq(category);
}

private BooleanExpression eqStatus(AuctionStatus status) {
  if (status == null) {
    return auction.status.notIn(AuctionStatus.FAILED,AuctionStatus.CANCELED);
  }
  return auction.status.eq(status);
}

private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
  if (minPrice == 0L && maxPrice == null) {
    return null; // 전체 가격 범위
  }

  // minPrice가 0보다 클 경우
  if(maxPrice == null){
    return auction.currentPrice.goe(minPrice);
  }
  return auction.currentPrice.between(minPrice, maxPrice);
}
  private BooleanExpression auctionStartNotificationCondition(
      Long userId
  ) {
    if (userId == null) {
      return Expressions.FALSE;
    }

    return notificationSetting.auction.eq(auction)
        .and(notificationSetting.user.id.eq(userId))
        .and(notificationSetting.type.eq(NotificationSettingType.AUCTION_START))
        .and(notificationSetting.activated.isTrue())
        .and(auction.status.eq(AuctionStatus.SCHEDULED));
  }

  private Expression<Boolean> isAuctionStartNotificationEnabled(Long userId) {
    if (userId == null) {
      return Expressions.constant(false);
    }

    return notificationSetting.id.isNotNull();
  }

}
