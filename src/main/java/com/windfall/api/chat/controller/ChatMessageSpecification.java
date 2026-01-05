package com.windfall.api.chat.controller;

import static com.windfall.global.exception.ErrorCode.FORBIDDEN_CHAT_ROOM;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_CHAT_ROOM;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_USER;

import com.windfall.api.chat.dto.response.ChatReadMarkResponse;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "ChatMessage", description = "채팅 메시지 API")
public interface ChatMessageSpecification {

  @ApiErrorCodes({NOT_FOUND_USER, NOT_FOUND_CHAT_ROOM, FORBIDDEN_CHAT_ROOM})
  @Operation(
      summary = "채팅 메시지 읽음 처리",
      description = "채팅방에 들어갈 때 호출. 해당 채팅방에서 '내가 보낸 메시지'를 제외한, 읽지 않은 메시지(isRead=false)를 모두 읽음 처리합니다."
  )
  ApiResponse<ChatReadMarkResponse> markChatMessagesAsRead(
      @Parameter(description = "채팅방 ID", example = "1")
      @PathVariable Long chatRoomId,

      @AuthenticationPrincipal CustomUserDetails userDetails
  );
}

