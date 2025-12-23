package com.windfall.domain.auction.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageStatus {
  TEMP("등록 대기"),
  ACTIVE("사용 중");

  private final String statusName;
}
