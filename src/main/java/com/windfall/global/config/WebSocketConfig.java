package com.windfall.global.config;

import com.windfall.global.websocket.StompAuthChannelInterceptor;
import com.windfall.global.websocket.WsHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
  private final WsHandshakeInterceptor wsHandshakeInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 인증 필요 endpoint
    registry.addEndpoint("/ws-stomp")
        .setAllowedOriginPatterns("*")
        .addInterceptors(wsHandshakeInterceptor)
        .withSockJS();

    // 인증 불필요 endpoint
    registry.addEndpoint("/ws-stomp-public")
        .setAllowedOriginPatterns("*")
        .addInterceptors(wsHandshakeInterceptor)
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompAuthChannelInterceptor);
  }
}
