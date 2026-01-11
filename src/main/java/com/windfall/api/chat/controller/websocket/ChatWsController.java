package com.windfall.api.chat.controller.websocket;

import com.windfall.api.chat.dto.request.ChatRoomPresenceRequest;
import com.windfall.api.chat.dto.websocket.ChatReadRequest;
import com.windfall.api.chat.dto.websocket.ChatSendRequest;
import com.windfall.api.chat.service.redis.ChatPresenceService;
import com.windfall.api.chat.service.websocket.ChatWsService;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

  private final ChatWsService chatWsService;
  private final ChatPresenceService chatPresenceService;

  @MessageMapping("/chat.send")
  public void send(ChatSendRequest request, Principal principal) {
    Long userId = extractUserId(principal);
    chatWsService.sendMessage(userId, request);
  }

  @MessageMapping("/chat.read")
  public void read(ChatReadRequest request, Principal principal) {
    Long userId = extractUserId(principal);
    chatWsService.markAsRead(userId, request.chatRoomId());
  }

  // 채팅방 화면 입장
  @MessageMapping("/chat.enter")
  public void enter(ChatRoomPresenceRequest request, Principal principal) {
    Long userId = extractUserId(principal);
    chatPresenceService.enterRoom(userId, request.chatRoomId());
  }

  // 채팅방 화면 나감
  @MessageMapping("/chat.leave")
  public void leave(ChatRoomPresenceRequest request, Principal principal) {
    Long userId = extractUserId(principal);
    chatPresenceService.leaveRoom(userId, request.chatRoomId());
  }

  // TTL 연장용 - 프론트가 30~60초마다 ping
  @MessageMapping("/chat.ping")
  public void ping(Principal principal) {
    Long userId = extractUserId(principal);
    chatPresenceService.touch(userId);
  }

  private Long extractUserId(Principal principal) {
    if (principal == null) {
      throw new ErrorException(ErrorCode.INVALID_TOKEN);
    }
    return Long.valueOf(principal.getName());
  }
}
