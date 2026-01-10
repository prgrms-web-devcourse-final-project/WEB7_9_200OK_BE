package com.windfall.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationSettingType {

  AUCTION_START("경매 시작"),
  AUCTION_END("경매 종료"),
  PRICE_REACHED("가격 도달"),
  REVIEW_REGISTERED("리뷰 등록");

  private final String description;
}