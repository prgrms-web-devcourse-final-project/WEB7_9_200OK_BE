package com.windfall.api.chat.controller.websocket;

import com.windfall.api.chat.dto.websocket.ChatReadRequest;
import com.windfall.api.chat.dto.websocket.ChatSendRequest;
import com.windfall.api.chat.service.websocket.ChatWsService;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.websocket.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

  private final ChatWsService chatWsService;

  @MessageMapping("/chat.send")
  public void send(ChatSendRequest request, SimpMessageHeaderAccessor accessor) {
    Long userId = getUserIdFromSession(accessor);
    chatWsService.sendMessage(userId, request);
  }

  @MessageMapping("/chat.read")
  public void read(ChatReadRequest request, SimpMessageHeaderAccessor accessor) {
    Long userId = getUserIdFromSession(accessor);
    chatWsService.markAsRead(userId, request.chatRoomId());
  }

  private Long getUserIdFromSession(SimpMessageHeaderAccessor accessor) {
    if (accessor.getSessionAttributes() == null) {
      throw new ErrorException(ErrorCode.INVALID_TOKEN);
    }
    Object v = accessor.getSessionAttributes().get(StompAuthChannelInterceptor.ATTR_WS_USER_ID);
    if (v == null) throw new ErrorException(ErrorCode.INVALID_TOKEN);
    if (v instanceof Long l) return l;
    if (v instanceof Integer i) return i.longValue();
    return Long.valueOf(String.valueOf(v));
  }
}
