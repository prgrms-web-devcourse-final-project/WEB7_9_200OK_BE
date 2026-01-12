package com.windfall.api.chat.event;

import com.windfall.api.chat.service.redis.ChatPresenceService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsSessionEventListener {

  private final ChatPresenceService chatPresenceService;

  @EventListener
  public void onDisconnect(org.springframework.web.socket.messaging.SessionDisconnectEvent event) {
    Principal principal = event.getUser();
    if (principal == null) return;

    try {
      Long userId = Long.valueOf(principal.getName());
      chatPresenceService.clear(userId);
    } catch (NumberFormatException ignored) {
    }
  }
}

