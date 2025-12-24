package com.windfall.api.chat.dto.response.info;

public record ChatInfo(
    Long roomId,
    int unreadCount
) {
}
