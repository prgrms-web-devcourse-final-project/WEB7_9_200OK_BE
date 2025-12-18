package com.windfall.api.chat.dto.request.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatRoomScope {
  ALL("전체 대화"),
  BUY("구매 대화"),
  SELL("판매 대화");

  private final String description;
}
