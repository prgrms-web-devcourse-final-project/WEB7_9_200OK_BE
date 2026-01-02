package com.windfall.global.websocket;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

  public static final String ATTR_ACCESS_TOKEN = "WS_ACCESS_TOKEN";

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes
  ) {
    if (request instanceof ServletServerHttpRequest servletReq) {
      HttpServletRequest http = servletReq.getServletRequest();
      Cookie[] cookies = http.getCookies();
      if (cookies != null) {
        Arrays.stream(cookies)
            .filter(c -> "accessToken".equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .ifPresent(token -> attributes.put(ATTR_ACCESS_TOKEN, token));
      }
    }
    return true;
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception
  ) {
  }
}
