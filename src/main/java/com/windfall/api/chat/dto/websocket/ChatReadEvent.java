package com.windfall.api.chat.dto.websocket;

import java.time.LocalDateTime;

public record ChatReadEvent(
    Long chatRoomId,
    Long readerId,
    Long lastReadMessageId,
    LocalDateTime readAt
) {
  public static ChatReadEvent of(Long chatRoomId, Long readerId, Long lastReadMessageId) {
    return new ChatReadEvent(chatRoomId, readerId, lastReadMessageId, LocalDateTime.now());
  }
}
