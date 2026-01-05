package com.windfall.global.websocket;

import com.windfall.api.user.service.JwtProvider;
import com.windfall.domain.user.enums.JwtValidationResult;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) return message;

    accessor.setLeaveMutable(true);

    String endpointType = getEndpointType(accessor);

    // 1) CONNECT
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      // 🔁 CHANGED: PUBLIC이면 토큰이 오더라도 "아예 무시"
      if ("PUBLIC".equals(endpointType)) {
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
      }

      // ✅ SECURED만 토큰 처리
      String token = resolveToken(accessor);

      if (token == null) {
        throw new ErrorException(ErrorCode.WS_TOKEN_MISSING);
      }

      JwtValidationResult result = jwtProvider.validateTokenWithResult(token);

      if (result == JwtValidationResult.EXPIRED) {
        throw new ErrorException(ErrorCode.WS_TOKEN_EXPIRED);
      }
      if (result == JwtValidationResult.INVALID) {
        throw new ErrorException(ErrorCode.WS_TOKEN_INVALID);
      }

      Long userId = jwtProvider.getUserId(token);
      if (userId == null) {
        throw new ErrorException(ErrorCode.WS_TOKEN_INVALID);
      }

      accessor.setUser(new StompPrincipal(String.valueOf(userId)));
    }

    // 2) SEND: 보호된 destination은 인증 강제
    if (StompCommand.SEND.equals(accessor.getCommand())) {
      String dest = accessor.getDestination();
      if (dest != null && (dest.startsWith("/app/chat.") || dest.startsWith("/app/auctions/"))) {
        requireAuthenticated(accessor);
      }
    }

    // 3) SUBSCRIBE: /user/** 는 무조건 인증 필요 (+ 채팅 topic 인증 필요)
    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      String dest = accessor.getDestination();
      if (dest == null) {
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
      }

      if (dest.startsWith("/user/")) {
        requireAuthenticated(accessor);
      }

      if (dest.startsWith("/topic/chat.rooms.")) {
        requireAuthenticated(accessor);
      }
    }

    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
  }

  private void requireAuthenticated(StompHeaderAccessor accessor) {
    if (accessor.getUser() == null) {
      throw new ErrorException(ErrorCode.WS_AUTH_REQUIRED);
    }
  }

  private String getEndpointType(StompHeaderAccessor accessor) {
    if (accessor.getSessionAttributes() == null) return "SECURED";
    Object v = accessor.getSessionAttributes().get(WsHandshakeInterceptor.ATTR_ENDPOINT_TYPE);
    if (v == null) return "SECURED";
    return String.valueOf(v);
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String auth = accessor.getFirstNativeHeader("Authorization");
    String token = extractBearer(auth);
    if (token != null) return token;

    if (accessor.getSessionAttributes() != null) {
      Object v = accessor.getSessionAttributes().get(WsHandshakeInterceptor.ATTR_ACCESS_TOKEN);
      if (v != null) return String.valueOf(v);
    }
    return null;
  }

  private String extractBearer(String authHeader) {
    if (authHeader == null) return null;
    String v = authHeader.trim();
    if (v.startsWith("Bearer ")) return v.substring(7).trim();
    return null;
  }
}
