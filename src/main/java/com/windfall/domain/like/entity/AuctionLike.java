package com.windfall.domain.like.entity;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "auction_like",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_auction_like_auction_user",
            columnNames = {"auction_id", "user_id"}
        )
    }
)
public class AuctionLike extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id", nullable = false)
  private Auction auction;

  @Column(nullable = false)
  private Long userId;

  public static AuctionLike create(Auction auction, Long userId) {
    return new AuctionLike(auction, userId);
  }
}