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

  private static final Long DEV_USER_ID = 1L;

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      String auth = accessor.getFirstNativeHeader("Authorization");
      String token = extractBearer(auth);

      // 토큰이 없으면:하드코딩 Principal
      if (token == null) {
        accessor.setUser(new StompPrincipal(DEV_USER_ID));
        return message;
      }

      // 토큰이 있으면: 검증 후 userId 기반 Principal 세팅(추후 인증 전환 대비)
      if (!jwtProvider.validateToken(token)) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      Long userId = jwtProvider.getUserId(token);
      if (userId == null) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      accessor.setUser(new StompPrincipal(userId));
    }

    if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
      Principal user = accessor.getUser();
      if (user != null) {
        // log.info("WS DISCONNECT userId={}", user.getName());
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

