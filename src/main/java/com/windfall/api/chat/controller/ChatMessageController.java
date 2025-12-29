package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.response.ChatReadMarkResponse;
import com.windfall.api.chat.service.ChatMessageService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-rooms")
public class ChatMessageController implements ChatMessageControllerSpecification {

  private final ChatMessageService chatMessageService;

  @Override
  @PatchMapping("/{chatRoomId}/messages/read")
  public ApiResponse<ChatReadMarkResponse> markChatMessagesAsRead(
      @PathVariable Long chatRoomId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUserId();
    ChatReadMarkResponse response = chatMessageService.markAsRead(chatRoomId, userId);
    return ApiResponse.ok("채팅방의 읽지 않은 메시지가 모두 읽음 처리되었습니다.", response);
  }
}

