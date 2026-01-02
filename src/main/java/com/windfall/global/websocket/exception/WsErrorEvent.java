package com.windfall.global.websocket.exception;

import java.time.LocalDateTime;

public record WsErrorEvent(
    String code,
    String message,
    LocalDateTime timestamp
) {
  public static WsErrorEvent of(String code, String message) {
    return new WsErrorEvent(code, message, LocalDateTime.now());
  }
}
