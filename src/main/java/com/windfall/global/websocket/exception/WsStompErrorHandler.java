package com.windfall.global.websocket.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsStompErrorHandler extends StompSubProtocolErrorHandler {

  private final ObjectMapper objectMapper;

  @Override
  public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

    Throwable root = unwrap(ex);

    // ✅ 1) 우리 커스텀 에러면: ErrorCode 기반으로 JSON 내려줌
    if (root instanceof ErrorException ee) {
      ErrorCode code = ee.getErrorCode();
      WsErrorEvent payload = WsErrorEvent.of(code.name(), code.getMessage());
      return buildErrorFrame(payload);
    }

    // ✅ 2) 그 외는 UNKNOWN_ERROR
    log.warn("WS processing error (unhandled): {}", root.toString(), root);
    WsErrorEvent payload = WsErrorEvent.of(ErrorCode.UNKNOWN_ERROR.name(), "웹소켓 처리 중 오류가 발생했습니다.");
    return buildErrorFrame(payload);
  }

  private Message<byte[]> buildErrorFrame(WsErrorEvent payload) {
    StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
    headers.setLeaveMutable(true);
    headers.setMessage(payload.message()); // STOMP 표준 message 헤더(보조)

    try {
      String json = objectMapper.writeValueAsString(payload);
      return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8), headers.getMessageHeaders());
    } catch (JsonProcessingException e) {
      // JSON 변환 실패 시 최소 메시지라도 내려줌
      String fallback = "{\"code\":\"" + payload.code() + "\",\"message\":\"" + payload.message() + "\"}";
      return MessageBuilder.createMessage(fallback.getBytes(StandardCharsets.UTF_8), headers.getMessageHeaders());
    }
  }

  private Throwable unwrap(Throwable ex) {
    // Spring WS 쪽에서 MessageDeliveryException으로 감싸지는 케이스가 많아서 벗김
    if (ex instanceof MessageDeliveryException mde && mde.getCause() != null) {
      return unwrap(mde.getCause());
    }
    if (ex.getCause() != null && ex.getCause() != ex) {
      // 2~3겹 더 감싸질 수도 있으니 한 번 더 안전하게
      return ex.getCause();
    }
    return ex;
  }
}
