package com.windfall.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

  private final RedisTemplate<String, Object> pubSubRedisTemplate;
  private final ChannelTopic channelTopic;

  public void publish(String destination, Object content) {
    RedisPubSubMessage message = new RedisPubSubMessage(destination, content);
    pubSubRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
  }
}
