package com.windfall.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

  // 채팅
  CHAT_MESSAGE("채팅 알림","chatRoom"),

  // 경매 시작
  AUCTION_START_WISHLIST("거래 시작","auction"),

  // 경매 유찰
  AUCTION_FAILED_SELLER("거래 유찰 (판매자)","auction"),
  AUCTION_FAILED_SUBSCRIBER("거래 유찰 (알림 설정 유저)","auction"),

  // 경매 낙찰
  SALE_SUCCESS_SELLER("경매 낙찰 (판매자)","auction"),
  SALE_SUCCESS_SUBSCRIBER("경매 낙찰 (알림 설정 유저)","auction"),

  // 가격 변동
  STOP_LOSS_TRIGGERED("스탑 로스 발생","auction"),
  PRICE_DROP("가격 하락","auction"),

  // 결제 성공
  PAYMENT_SUCCESS_BUYER("결제 성공 (구매자)","auction"),

  // 구매 확정
  PURCHASE_CONFIRMED_SELLER("구매 확정 (판매자)","payment"),

  // 리뷰
  REVIEW_REGISTERED("리뷰 등록","review");

  private final String description;
  private final String target;
}
