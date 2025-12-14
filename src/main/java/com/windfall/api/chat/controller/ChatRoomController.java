package com.windfall.api.chat.controller;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.service.ChatRoomService;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ApiResponse<ChatRoomListResponse> getChatRooms(
      @RequestParam ChatRoomScope scope,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "1") Long userId
  ) {

    ChatRoomListResponse response = chatRoomService.getChatRooms(userId, scope, page, size);
    return ApiResponse.ok("채팅방 목록이 조회되었습니다.", response);
  }
}
