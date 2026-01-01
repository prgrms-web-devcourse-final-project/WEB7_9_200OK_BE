package com.windfall.api.chat.controller.websocket;

import com.windfall.api.chat.dto.websocket.ChatSendRequest;
import com.windfall.api.chat.service.websocket.ChatWsService;
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
    Long userId = Long.valueOf(principal.getName());
    chatWsService.sendMessage(userId, request);
  }

}
