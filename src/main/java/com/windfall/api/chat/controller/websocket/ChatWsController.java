package com.windfall.api.chat.controller.websocket;

import com.windfall.api.chat.dto.websocket.ChatReadRequest;
import com.windfall.api.chat.dto.websocket.ChatSendRequest;
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

  @MessageMapping("/chat.send")
  public void send(ChatSendRequest request, Principal principal) {
    if (principal == null) {
      throw new ErrorException(ErrorCode.INVALID_TOKEN);
    }
    Long userId = Long.valueOf(principal.getName());
    chatWsService.sendMessage(userId, request);
  }

  @MessageMapping("/chat.read")
  public void read(ChatReadRequest request, Principal principal) {
    if (principal == null) {
      throw new ErrorException(ErrorCode.INVALID_TOKEN);
    }
    Long userId = Long.valueOf(principal.getName());
    chatWsService.markAsRead(userId, request.chatRoomId());
  }

}
