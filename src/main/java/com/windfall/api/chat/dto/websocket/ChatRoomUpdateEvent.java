package com.windfall.api.chat.dto.websocket;

import com.windfall.domain.chat.enums.ChatMessageType;
import java.time.LocalDateTime;

public record ChatRoomUpdateEvent(
    Long chatRoomId,
    String lastMessagePreview,
    ChatMessageType lastMessageType,
    LocalDateTime lastMessageAt,
    long unreadCountDelta,
    Boolean resetUnread
) {
  public static ChatRoomUpdateEvent of(
      Long chatRoomId,
      String lastMessagePreview,
      ChatMessageType lastMessageType,
      LocalDateTime lastMessageAt,
      long unreadCountDelta,
      Boolean resetUnread
  ) {
    return new ChatRoomUpdateEvent(
        chatRoomId,
        lastMessagePreview,
        lastMessageType,
        lastMessageAt,
        unreadCountDelta,
        resetUnread
    );
  }
}

