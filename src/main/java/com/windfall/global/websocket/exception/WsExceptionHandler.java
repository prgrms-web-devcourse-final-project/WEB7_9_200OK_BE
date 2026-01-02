package com.windfall.global.websocket.exception;

import com.windfall.global.exception.ErrorException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class WsExceptionHandler {

  private final SimpMessagingTemplate messagingTemplate;

  @MessageExceptionHandler(ErrorException.class)
  public void handleErrorException(ErrorException e, Principal principal) {
    // principal이 없으면 user queue로 못 보냄
    if (principal == null) return;

    WsErrorEvent payload = WsErrorEvent.of(
        e.getErrorCode().name(),
        e.getErrorCode().getMessage()
    );

    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", payload);
  }

  @MessageExceptionHandler(Exception.class)
  public void handleAny(Exception e, Principal principal) {
    if (principal == null) return;

    WsErrorEvent payload = WsErrorEvent.of("UNKNOWN_ERROR", e.getMessage());
    messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", payload);
  }
}
