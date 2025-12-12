package com.windfall.domain.recentview.entity;


import com.windfall.domain.auction.entity.Auction;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentView extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id", nullable = false)
  private Auction auction;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder
  public RecentView(Auction auction, Long userId) {
    this.auction = auction;
    this.userId = userId;
  }

  public static RecentView createRecentView(Auction auction, Long userId){
    return RecentView.builder()
        .auction(auction)
        .userId(userId)
        .build();
  }
}
