package com.windfall.api.tag.factory;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.user.entity.User;
import java.time.LocalDateTime;


public class AuctionFactory {

  private AuctionFactory() {
    // 인스턴스화 방지
  }

  public static Auction createTestAuction(User seller) {
    return Auction.builder()
        .title("테스트 제목")
        .description("테스트 설명")
        .category(AuctionCategory.DIGITAL)
        .startPrice(10000L)
        .currentPrice(10000L)
        .stopLoss(9000L)
        .dropAmount(50L)
        .status(AuctionStatus.SCHEDULED)
        .startedAt(LocalDateTime.now().plusDays(2))
        .seller(seller)
        .build();
  }
}