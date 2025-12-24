package com.windfall.domain.notification.entity;

import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private NotificationSetting setting;

  @Column(nullable = false)
  private Long priceAlert;
}
