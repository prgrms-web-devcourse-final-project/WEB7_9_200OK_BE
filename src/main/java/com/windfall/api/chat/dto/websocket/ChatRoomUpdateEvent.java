package com.windfall.api.chat.dto.websocket;

import com.windfall.domain.chat.enums.ChatMessageType;
import java.time.LocalDateTime;

public record ChatRoomUpdateEvent(
    Long chatRoomId,
    String lastMessagePreview,
    ChatMessageType lastMessageType,
    LocalDateTime lastMessageAt,
    long unreadCount
) {
  public static ChatRoomUpdateEvent of(
      Long chatRoomId,
      String lastMessagePreview,
      ChatMessageType lastMessageType,
      LocalDateTime lastMessageAt,
      long unreadCount
  ) {
    return new ChatRoomUpdateEvent(
        chatRoomId,
        lastMessagePreview,
        lastMessageType,
        lastMessageAt,
        unreadCount
    );
  }
}

