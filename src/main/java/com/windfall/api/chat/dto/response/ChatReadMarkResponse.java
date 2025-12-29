package com.windfall.api.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 읽음 처리 응답 DTO")
public record ChatReadMarkResponse(

    @Schema(description = "업데이트된 읽지 않은 메시지 수")
    int updateCount
) {
    public static ChatReadMarkResponse of(int updatedCount) {
        return new ChatReadMarkResponse(updatedCount);
    }
}
