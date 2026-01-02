package com.windfall.domain.like.repository;

import java.util.List;

public interface AuctionLikeRepositoryCustom {

  // 사용자가 찜한 auctionId 목록 조회
  List<Long> findLikedAuctionIdsByActivatedTrue(Long userId, List<Long> auctionIds);
}