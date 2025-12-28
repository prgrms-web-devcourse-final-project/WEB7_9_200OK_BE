package com.windfall.api.chat.dto.response.info;

import com.windfall.domain.chat.enums.ChatMessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "채팅 메시지 정보 DTO")
public record ChatMessageItem(

    @Schema(description = "메시지 ID")
    Long messageId,

    @Schema(description = "보낸 사람 ID")
    Long senderId,

    @Schema(description = "내 메시지 여부")
    boolean isMine,

    @Schema(description = "메시지 타입")
    ChatMessageType messageType,

    @Schema(description = "텍스트 내용(TEXT일 때)")
    String content,

    @Schema(description = "이미지 URL 목록(IMAGE일 때)")
    List<String> imageUrls,

    @Schema(description = "읽음 여부(상대방이 읽었는지)")
    boolean isRead,

    @Schema(description = "전송 시각")
    LocalDateTime createdAt
) {}
