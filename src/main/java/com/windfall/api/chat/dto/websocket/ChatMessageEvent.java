package com.windfall.api.chat.dto.websocket;

import com.windfall.domain.chat.enums.ChatMessageType;
import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageEvent(
    Long chatRoomId,
    Long messageId,
    Long senderId,
    ChatMessageType messageType,
    String content,
    List<String> imageUrls,
    boolean isRead,
    LocalDateTime createdAt
) {}

