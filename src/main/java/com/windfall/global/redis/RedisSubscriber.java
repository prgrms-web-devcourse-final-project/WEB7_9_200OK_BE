package com.windfall.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      RedisPubSubMessage pubSubMessage = objectMapper.readValue(
          message.getBody(),
          RedisPubSubMessage.class
      );

      messagingTemplate.convertAndSend(
          pubSubMessage.destination(),
          pubSubMessage.content()
      );

    } catch (Exception e) {
      log.error("Failed to process Redis message: {}", e.getMessage(), e);
    }
  }
}
