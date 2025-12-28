package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.response.ChatReadMarkResponse;
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

  private final ChatMessageReadService chatMessageReadService;

  @Override
  @PatchMapping("/{chatRoomId}/messages/read")
  public ApiResponse<ChatReadMarkResponse> markChatMessagesAsRead(
      @PathVariable Long chatRoomId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUserId();
    ChatReadMarkResponse response = chatMessageReadService.markAsRead(chatRoomId, userId);
    return ApiResponse.ok(response);
  }
}

