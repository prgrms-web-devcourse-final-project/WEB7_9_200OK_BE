package com.windfall.domain.auction.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuctionStatus {

  SCHEDULED("경매 예정"),
  PROCESS("경매 진행 중"),
  COMPLETED("낙찰 완료"),
  FAILED("유찰"),
  CANCELED("경매 취소");

  private final String description;
}
