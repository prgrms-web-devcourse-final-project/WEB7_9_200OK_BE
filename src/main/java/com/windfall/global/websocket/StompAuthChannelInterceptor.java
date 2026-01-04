package com.windfall.global.websocket;

import com.windfall.api.user.service.JwtProvider;
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

    // 1) CONNECT: 토큰 있으면 Principal을 "userId"로 강제 세팅
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      // Authorization or 쿠키 fallback
      String token = resolveToken(accessor);

      if (token != null) {
        if (!jwtProvider.validateToken(token)) {
          throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtProvider.getUserId(token);
        if (userId == null) {
          throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }

        // Principal을 userId로 세팅
        accessor.setUser(new StompPrincipal(String.valueOf(userId)));
      }
    }

    // 2) SEND/SUBSCRIBE에서 인증 강제
    if (StompCommand.SEND.equals(accessor.getCommand())) {
      String dest = accessor.getDestination();
      if (dest != null && (dest.startsWith("/app/chat.") || dest.startsWith("/app/auctions/"))) {
        requireAuthenticated(accessor);
      }
    }

    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      String dest = accessor.getDestination();
      if (dest != null && dest.startsWith("/user/")) {
        requireAuthenticated(accessor);
      }
    }

    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
  }

  private void requireAuthenticated(StompHeaderAccessor accessor) {
    if (accessor.getUser() == null) {
      throw new ErrorException(ErrorCode.INVALID_TOKEN);
    }
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    // 1) Authorization 헤더 우선
    String auth = accessor.getFirstNativeHeader("Authorization");
    String token = extractBearer(auth);
    if (token != null) return token;

    // 2) 쿠키 fallback (HandshakeInterceptor)
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
