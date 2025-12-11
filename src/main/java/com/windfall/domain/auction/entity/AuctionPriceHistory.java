package com.windfall.domain.auction.entity;

import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuctionPriceHistory extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id", nullable = false)
  private Auction auction;

  @Column(nullable = false)
  private Long price;

  @Column(nullable = false)
  private Long viewerCount;

  public static AuctionPriceHistory create(Auction auction, Long price, Long viewerCount) {
    return AuctionPriceHistory.builder()
        .auction(auction)
        .price(price)
        .viewerCount(viewerCount)
        .build();
  }
}
