package com.windfall.api.auction.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionViewerService {

  private final RedisTemplate<String, String> redisTemplate;

  private String getViewerKey(Long auctionId) {
    return "auction:" + auctionId + ":viewers";
  }

  private String getSessionKey(String sessionId) {
    return "session:" + sessionId + ":auction";
  }

  public long addViewer(Long auctionId, String sessionId) {
    redisTemplate.opsForSet().add(getViewerKey(auctionId), sessionId);
    redisTemplate.opsForValue().set(getSessionKey(sessionId), String.valueOf(auctionId), Duration.ofHours(3));

    return getViewerCount(auctionId);
  }

  public Long removeViewer(String sessionId) {
    String auctionIdStr = redisTemplate.opsForValue().get(getSessionKey(sessionId));

    if (auctionIdStr == null) {
      return null;
    }

    Long auctionId = Long.valueOf(auctionIdStr);

    redisTemplate.opsForSet().remove(getViewerKey(auctionId), sessionId);
    redisTemplate.delete(getSessionKey(sessionId));

    return auctionId;
  }

  public long getViewerCount(Long auctionId) {
    Long size = redisTemplate.opsForSet().size(getViewerKey(auctionId));

    if(size == null) {
      return 0L;
    }
    return size;
  }
}