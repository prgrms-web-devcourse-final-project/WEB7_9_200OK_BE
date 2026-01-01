package com.windfall.global.websocket;

import com.windfall.api.user.service.JwtProvider;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    StompCommand cmd = accessor.getCommand();

    if (cmd == null) return message;

    if (StompCommand.CONNECT.equals(cmd)) {

      // 1) STOMP Native Header에서 Authorization 가져오기
      String auth = accessor.getFirstNativeHeader("Authorization");
      String token = extractBearer(auth);

      // 2) 없으면 HandshakeInterceptor가 넣어둔 쿠키 토큰 fallback
      if (token == null && accessor.getSessionAttributes() != null) {
        Object v = accessor.getSessionAttributes().get(WsHandshakeInterceptor.ATTR_ACCESS_TOKEN);
        if (v != null) token = String.valueOf(v);
      }

      // 토큰 없으면 CONNECT 자체 실패
      if (token == null || token.isBlank()) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      if (!jwtProvider.validateToken(token)) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      Long userId = jwtProvider.getUserId(token);
      if (userId == null) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      accessor.setUser(new StompPrincipal(userId));
      return message;
    }

    // CONNECT 이후 들어오는 프레임은 "반드시" Principal이 존재
    if (StompCommand.SEND.equals(cmd) || StompCommand.SUBSCRIBE.equals(cmd) || StompCommand.UNSUBSCRIBE.equals(cmd)) {
      Principal user = accessor.getUser();
      if (user == null) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }
    }

    return message;
  }

  private String extractBearer(String authHeader) {
    if (authHeader == null) return null;
    if (authHeader.startsWith("Bearer ")) return authHeader.substring(7);
    return null;
  }
}

