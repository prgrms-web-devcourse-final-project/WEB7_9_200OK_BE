package com.windfall.domain.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageType {
  TEXT("텍스트"),
  IMAGE("이미지"),
  SYSTEM("시스템 메시지");

  private final String description;
}
