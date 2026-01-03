package com.windfall.domain.trade.entity;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Trade extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id", nullable = false)
  private Auction auction;

  @Column(name = "buyer_id", nullable = false)
  private Long buyerId;

  @Column(name = "seller_id", nullable = false)
  private Long sellerId;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TradeStatus status = TradeStatus.PENDING;

  @Column(name = "final_price", nullable = false)
  private Long finalPrice;

  public void changeStatus(TradeStatus newStatus) {
    this.status = newStatus;
  }

  public void changeBuyer(Long buyerId){
    this.buyerId = buyerId;
  }

}
