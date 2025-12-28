package com.windfall.api.chat.dto.response;

import com.windfall.api.chat.dto.response.info.ChatMessageInfo;
import com.windfall.api.chat.dto.response.info.ChatRoomMetaInfo;
import com.windfall.global.response.CursorResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 상세 조회 응답 DTO")
public record ChatRoomDetailResponse(

    @Schema(description = "채팅방 메타 정보")
    ChatRoomMetaInfo chatRoomMeta,

    @Schema(description = "메시지 커서 페이징")
    CursorResponse<ChatMessageInfo> messages
) {
  public static ChatRoomDetailResponse of(ChatRoomMetaInfo meta, CursorResponse<ChatMessageInfo> messages) {
    return new ChatRoomDetailResponse(meta, messages);
  }
}
