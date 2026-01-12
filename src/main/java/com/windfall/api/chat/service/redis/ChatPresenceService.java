package com.windfall.api.chat.service.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatPresenceService {

  private static final String KEY_PREFIX = "chat:activeRoom:"; // chat:activeRoom:{userId} 키 설정
  private static final Duration TTL = Duration.ofMinutes(5);   // 비정상 종료 대비 TTL 설정

  private final RedisTemplate<String, String> redisTemplate;

  public void enterRoom(Long userId, Long chatRoomId) {
    String key = key(userId);
    redisTemplate.opsForValue().set(key, String.valueOf(chatRoomId), TTL);
  }

  public void leaveRoom(Long userId, Long chatRoomId) {
    // 현재 저장된 방이 해당 방이면 삭제 (다른 방 들어간 상태면 건드리지 않음)
    String activeRoomKey = key(userId);
    String currentRoomIdValue = redisTemplate.opsForValue().get(activeRoomKey);
    if (currentRoomIdValue != null && currentRoomIdValue.equals(String.valueOf(chatRoomId))) {
      redisTemplate.delete(activeRoomKey);
    }
  }

  public void clear(Long userId) {
    redisTemplate.delete(key(userId));
  }

  public Long getActiveRoomId(Long userId) {
    String activeRoomIdValue = redisTemplate.opsForValue().get(key(userId));
    if (activeRoomIdValue == null || activeRoomIdValue.isBlank()) return null;
    try {
      return Long.valueOf(activeRoomIdValue);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  public boolean isViewingRoom(Long userId, Long chatRoomId) {
    Long active = getActiveRoomId(userId);
    return active != null && active.equals(chatRoomId);
  }

  public void touch(Long userId) {
    // TTL 연장용 (핑 처리)
    String activeRoomKey = key(userId);
    String currentRoomIdValue = redisTemplate.opsForValue().get(activeRoomKey);
    if (currentRoomIdValue != null) {
      redisTemplate.opsForValue().set(activeRoomKey, currentRoomIdValue, TTL);
    }
  }

  private String key(Long userId) {
    return KEY_PREFIX + userId;
  }
}

