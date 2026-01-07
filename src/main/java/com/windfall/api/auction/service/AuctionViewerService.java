package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.response.info.PopularInfo;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionViewerService {

  private final RedisTemplate<String, String> redisTemplate;
  private final AuctionRepository auctionRepository;

  private String getViewerKey(Long auctionId) {
    return "auction:" + auctionId + ":viewers";
  }

  private String getSessionKey(String sessionId) {
    return "session:" + sessionId + ":auction";
  }
  private static final String RANKING_KEY = "auction:rankings";

  public long addViewer(Long auctionId, String sessionId) {
    redisTemplate.opsForSet().add(getViewerKey(auctionId), sessionId);
    redisTemplate.opsForValue().set(getSessionKey(sessionId), String.valueOf(auctionId), Duration.ofHours(3));

    // 추가 코드
    long viewerCount = getViewerCount(auctionId);

    updateRanking(auctionId, viewerCount);

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

    // 추가 코드
    long viewerCount = getViewerCount(auctionId);

    if (viewerCount == 0) {
      redisTemplate.opsForZSet()
          .remove(RANKING_KEY, auctionIdStr);
    } else {
      updateRanking(auctionId, viewerCount);
    }

    return auctionId;
  }

  public long getViewerCount(Long auctionId) {
    Long size = redisTemplate.opsForSet().size(getViewerKey(auctionId));

    if(size == null) {
      return 0L;
    }
    return size;
  }

  // 재시도 로직은 AOP가 되어야 작동이 되는 문제로 메소드를 여기에 두었습니다.
  @Retry(name = "customRetry", fallbackMethod = "fallbackPopularInfo")
  public List<PopularInfo> getPopularInfo(){
    List<Long> rankedIds = getTop15AuctionIds();

    List<PopularInfo> popularList = auctionRepository.getPopularInfo(rankedIds);

    if(popularList.isEmpty() || rankedIds.isEmpty()){
      throw new ErrorException(ErrorCode.POPULAR_AUCTION_FETCH_FAIL);
    }

    Map<Long, Integer> orderMap = new HashMap<>();

    for (int i = 0; i < rankedIds.size(); i++) {
      orderMap.put(rankedIds.get(i), i);
    }

    popularList.sort(
        Comparator.comparingInt(a -> orderMap.get(a.auctionId()))
    );

    return popularList;
  }

  private List<PopularInfo> fallbackPopularInfo(Exception e){
    return auctionRepository.fallbackPopularInfo(AuctionStatus.PROCESS,15);
  }

  public List<Long> getTop15AuctionIds() {
    Set<String> topAuctionIds =
        redisTemplate.opsForZSet()
            .reverseRange(RANKING_KEY, 0, 14);

    if (topAuctionIds == null || topAuctionIds.isEmpty()) {
      return List.of();
    }

    return topAuctionIds.stream()
        .map(Long::valueOf)
        .toList();
  }

  private void updateRanking(Long auctionId, long viewerCount) {
    redisTemplate.opsForZSet()
        .add(RANKING_KEY, String.valueOf(auctionId), viewerCount);
  }
}