package com.windfall.api.chat.dto.websocket;

import com.windfall.domain.chat.enums.ChatMessageType;
import java.util.List;

public record ChatSendRequest(

    Long chatRoomId,
    ChatMessageType chatMessageType,
    String content,
    List<String> imageUrls
) {}
