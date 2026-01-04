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

    // 세션이 어느 엔드포인트로 열렸는지 확인(SECURED | PUBLIC)
    String endpointType = getEndpointType(accessor);

    // 1) CONNECT: SECURED면 토큰 없으면 여기서 끊음
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor);

      if ("SECURED".equals(endpointType)) {
        if (token == null) {
          throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }
      }

      // 토큰이 있으면(SECURED든 PUBLIC이든) 인증 세팅
      if (token != null) {
        if (!jwtProvider.validateToken(token)) {
          throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }
        Long userId = jwtProvider.getUserId(token);
        if (userId == null) {
          throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }

        accessor.setUser(new StompPrincipal(String.valueOf(userId)));
      }
    }

    // 2) SEND: 보호된 destination은 인증 강제
    if (StompCommand.SEND.equals(accessor.getCommand())) {
      String dest = accessor.getDestination();
      if (dest != null && (dest.startsWith("/app/chat.") || dest.startsWith("/app/auctions/"))) {
        requireAuthenticated(accessor);
      }
    }

    // 3) SUBSCRIBE: /user/** 는 무조건 인증 필요
    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      String dest = accessor.getDestination();
      if (dest == null) return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());

      // 개인 큐는 인증 필수
      if (dest.startsWith("/user/")) {
        requireAuthenticated(accessor);
      }

      // 채팅방 topic도 인증 없이는 구독 불가
      if (dest.startsWith("/topic/chat.rooms.")) {
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

  private String getEndpointType(StompHeaderAccessor accessor) {
    if (accessor.getSessionAttributes() == null) return "SECURED";
    Object v = accessor.getSessionAttributes().get(WsHandshakeInterceptor.ATTR_ENDPOINT_TYPE);
    if (v == null) return "SECURED";
    return String.valueOf(v);
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

