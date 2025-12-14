package com.windfall.api.chat.dto.response.info;

import com.windfall.domain.chat.enums.ChatMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "마지막 메시지 정보 DTO")
public record LastMessageInfo(

    @Schema(description = "마지막 메시지 시간")
    LocalDateTime lastMessageAt,

    @Schema(description = "마지막 메시지 프리뷰")
    String preview,

    @Schema(description = "마지막 메시지 타입")
    ChatMessageType type
) {}
