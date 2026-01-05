package com.windfall.domain.notification.entity;

import com.windfall.global.entity.BaseEntity;
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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PriceNotification extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private NotificationSetting setting;

  private Long auctionId;  // join 회피 용

  private Long userId;  // join 회피 용

  private Long targetPrice;

  private Boolean notified;

  public void updateTargetPrice(Long price) {
    targetPrice = price;
  }

  public void resetNotified() {
    notified = false;
  }
}
