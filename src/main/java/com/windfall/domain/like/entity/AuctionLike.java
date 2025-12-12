package com.windfall.domain.like.entity;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionLike extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "auction_id", nullable = false)
  private Auction auction;

  @Column(nullable = false)
  private Long userId;
}