package com.windfall.domain.notification.entity;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.notification.enums.NotificationSettingType;
import com.windfall.domain.user.entity.User;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "auction_id", "type"}
    )
)
public class NotificationSetting extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Auction auction;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationSettingType type;

  public static NotificationSetting create(
      User user,
      Auction auction,
      NotificationSettingType type
  ) {
    NotificationSetting setting = NotificationSetting.builder()
        .user(user)
        .auction(auction)
        .type(type)
        .build();

    setting.updateActivated(false);
    return setting;
  }

  public void updateActivated(boolean activated) {
    this.setActivated(activated);
  }
}