package com.windfall.domain.auction.entity;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.user.entity.User;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE auction SET activated = false WHERE id = ?")
@SQLRestriction("activated = true")
public class Auction extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuctionCategory category;

  @Column(nullable = false)
  private Long startPrice;

  @Column(nullable = false)
  private Long currentPrice;

  @Column(nullable = false)
  private Long stopLoss;

  @Column(nullable = false)
  private Long dropAmount;

  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuctionStatus status = AuctionStatus.SCHEDULED;

  @Column(nullable = false)
  private LocalDateTime startedAt;

  private LocalDateTime endedAt;

  public static Auction create(AuctionCreateRequest request, User seller) {
    return Auction.builder()
        .seller(seller)
        .title(request.title())
        .description(request.description())
        .category(request.category())
        .startPrice(request.startPrice())
        .currentPrice(request.startPrice())
        .stopLoss(request.stopLoss())
        .dropAmount(request.dropAmount())
        .startedAt(request.startAt())
        .build();
  }

  public void updateStatus(AuctionStatus status) {
    this.status = status;
  }

  public void updateCurrentPrice(Long price) {
    this.currentPrice = price;
  }

  public long getDisplayPrice() {
    if (this.status == AuctionStatus.SCHEDULED) {
      return this.startPrice;
    }
    return this.currentPrice;
  }

  public double calculateDiscountRate() {
    if (startPrice == 0) {
      return 0.0;
    }
    double rate = ((double) (this.startPrice - getDisplayPrice()) / this.startPrice) * 100;

    return Math.round(rate * 10.0) / 10.0;
  }

  public boolean isSeller(Long userId) {
    if (userId == null) {
      return false;
    }

    return this.seller.getId().equals(userId);
  }

  public void start() {
    if (this.status == AuctionStatus.SCHEDULED) {
      this.status = AuctionStatus.PROCESS;
    }
  }

  public void declinePrice(long minutesElapsed) {
    if (this.status != AuctionStatus.PROCESS) return;

    long dropCount = minutesElapsed / 5;
    if (dropCount <= 0) return;

    long totalDiscount = dropCount * this.dropAmount;
    long targetPrice = this.startPrice - totalDiscount;

    if (targetPrice < this.stopLoss) {
      this.currentPrice = this.stopLoss;
      this.status = AuctionStatus.FAILED;
    }
    else if (targetPrice < this.currentPrice) {
      this.currentPrice = targetPrice;
    }
  }
}
