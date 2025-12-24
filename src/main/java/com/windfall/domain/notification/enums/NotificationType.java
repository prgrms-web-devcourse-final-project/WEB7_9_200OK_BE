package com.windfall.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

  // 채팅
  CHAT_MESSAGE("채팅 알림"),

  // 경매 시작
  AUCTION_START_WISHLIST("거래 시작"),

  // 경매 유찰
  AUCTION_FAILED_SELLER("거래 유찰 (판매자)"),
  AUCTION_FAILED_BUYER("거래 유찰 (구매자)"),

  // 경매 종료 (내 물건 아님)
  AUCTION_ENDED_OTHER("남의 물건 경매 종료"),

  // 가격 변동
  STOP_LOSS_TRIGGERED("스탑 로스 발생"),
  PRICE_DROP("가격 하락"),

  // 거래 성공
  SALE_SUCCESS_SELLER("판매 성공 (판매자)"),
  PAYMENT_SUCCESS_BUYER("결제 성공 (구매자)"),

  // 구매 확정
  PURCHASE_CONFIRMED_SELLER("구매 확정 (판매자)"),

  // 리뷰
  REVIEW_REGISTERED("리뷰 등록");

  private final String description;
}
