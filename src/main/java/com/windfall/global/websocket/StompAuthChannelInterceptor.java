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
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  public static final String ATTR_WS_USER_ID = "WS_USER_ID";

  private final JwtProvider jwtProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      String auth = accessor.getFirstNativeHeader("Authorization");
      String token = extractBearer(auth);

      if (token == null) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      if (!jwtProvider.validateToken(token)) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      Long userId = jwtProvider.getUserId(token);
      if (userId == null) {
        throw new ErrorException(ErrorCode.INVALID_TOKEN);
      }

      if (accessor.getSessionAttributes() != null) {
        accessor.getSessionAttributes().put(ATTR_WS_USER_ID, userId);
      }

      // ⚠️ 여기서 accessor.setUser(new StompPrincipal(userId))를 해도,
      // Spring Security 메시징 인터셉터가 덮어써서 user-name이 KAKAO_...로 남을 수 있어.
      // (그래서 우리는 principal name을 숫자로 만들려고 애쓰지 않고, userId는 세션에서 꺼내 쓰는 전략을 씀)
    }

    return message;
  }

  private String extractBearer(String authHeader) {
    if (authHeader == null) return null;
    String v = authHeader.trim();

    if (v.startsWith("Bearer ")) return v.substring(7).trim();

    return null;
  }
}