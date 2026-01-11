package com.windfall.api.chat.event.vo;

import java.time.LocalDateTime;

public record ChatMessageCreatedEvent(
    Long chatRoomId,
    Long senderId,
    Long receiverId,
    String senderName,
    String preview,
    LocalDateTime createdAt
) {
  public static ChatMessageCreatedEvent of(
      Long chatRoomId,
      Long senderId,
      Long receiverId,
      String senderName,
      String preview,
      LocalDateTime createdAt
  ) {
    return new ChatMessageCreatedEvent(chatRoomId, senderId, receiverId, senderName, preview, createdAt);
  }
}

