package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.dto.response.ChatRoomDetailResponse;
import com.windfall.api.chat.dto.response.ChatRoomListResponse;
import com.windfall.api.chat.service.ChatRoomService;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController implements ChatRoomSpecification {

  private final ChatRoomService chatRoomService;

  @Override
  @GetMapping
  public ApiResponse<List<ChatRoomListResponse>> getChatRooms(
      @RequestParam(defaultValue = "ALL") ChatRoomScope scope,
      @RequestParam(defaultValue = "1") Long userId
  ) {

    List<ChatRoomListResponse> response = chatRoomService.getChatRooms(userId, scope);
    return ApiResponse.ok("채팅방 목록이 조회되었습니다.", response);
  }

  @Override
  @GetMapping("/{chatRoomId}/messages")
  public ApiResponse<ChatRoomDetailResponse> getChatRoomDetail(
      @PathVariable Long chatRoomId,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "20") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUserId();
    ChatRoomDetailResponse response = chatRoomService.getChatRoomDetail(userId, chatRoomId, cursor,
        size);
    return ApiResponse.ok("채팅방 상세 정보가 조회되었습니다.", response);
  }
}
