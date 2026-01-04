package com.windfall.domain.like.repository;

import static com.windfall.domain.like.entity.QAuctionLike.auctionLike;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionLikeRepositoryCustomImpl implements AuctionLikeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Long> findLikedAuctionIdsByActivatedTrue(Long userId, List<Long> auctionIds) {

    if (auctionIds.isEmpty()) {
      return List.of();
    }

    return queryFactory
        .select(auctionLike.auction.id)
        .from(auctionLike)
        .where(
            auctionLike.userId.eq(userId),
            auctionLike.auction.id.in(auctionIds),
            auctionLike.activated.isTrue()
        )
        .fetch();
  }
}