package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCancelResponse;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionService {

  private final AuctionRepository auctionRepository;
  private final AuctionPriceHistoryRepository historyRepository;
  private final UserService userService;

  private final TagRepository tagRepository;
  private final AuctionTagRepository auctionTagRepository;

  private final RedisTemplate<String, String> redisTemplate;

  @Transactional
  public AuctionCreateResponse createAuction(AuctionCreateRequest request) {
    User seller = userService.getUserById(request.sellerId());

    validateAuctionRequest(request);

    Auction auction = Auction.create(request, seller);

    Auction savedAuction = auctionRepository.save(auction);

    // TODO: 태그 저장 로직 추가
    saveAuctionTags(savedAuction, request.tags());

    return AuctionCreateResponse.from(savedAuction, seller.getId());
  }

  private void validateAuctionRequest(AuctionCreateRequest request) {
    if (request.startPrice() * 0.9 < request.stopLoss()) {
      throw new ErrorException(ErrorCode.INVALID_STOP_LOSS);
    }

    if (request.startPrice() > request.dropAmount() * 200) {
      throw new ErrorException(ErrorCode.INVALID_DROP_AMOUNT);
    }

    if (request.startAt().isBefore(LocalDateTime.now().plusDays(1L))
        || request.startAt().isAfter(LocalDateTime.now().plusDays(7L))) {
      throw new ErrorException(ErrorCode.INVALID_TIME);
    }
  }

  private void saveAuctionTags(Auction auction, List<String> tagNames) {
    if (tagNames == null || tagNames.isEmpty()) {
      return;
    }

    for (String tagName : tagNames) {
      String normalizedName = tagName.trim();

      Tag tag = tagRepository.findByTagName(normalizedName)
          .orElseGet(() -> tagRepository.save(Tag.create(normalizedName))
          );

      AuctionTag auctionTag = AuctionTag.create(auction, tag);
      auctionTagRepository.save(auctionTag);
    }
  }

  @Transactional
  public AuctionCancelResponse cancelAuction(Long auctionId, Long userId) {
    User user = userService.getUserById(userId);

    Auction auction = getAuctionById(auctionId);

    validateCancelAuction(auction, user);

    auction.updateStatus(AuctionStatus.CANCELED);
    return AuctionCancelResponse.of(auctionId, AuctionStatus.CANCELED);
  }

  private void validateCancelAuction(Auction auction, User user) {
    validateIsSeller(auction,user);

    // TODO 추가 필요: trade가 생길 시 trade 구매완료 상태값 받아오기
    if(auction.getStatus() != AuctionStatus.SCHEDULED){
      throw new ErrorException(ErrorCode.AUCTION_CANNOT_CANCEL);
    }
  }

  @Transactional
  public void deleteAuction(Long auctionId, Long userId) {
    User user = userService.getUserById(userId);

    Auction auction = getAuctionById(auctionId);

    validateDeleteAuction(auction, user);

    auctionRepository.deleteById(auctionId);
  }

  private void validateDeleteAuction(Auction auction, User user) {
    validateIsSeller(auction,user);

    // TODO 추가 필요: trade가 생길 시 trade 구매완료 상태값 받아오기
    if(auction.getStatus() != AuctionStatus.SCHEDULED
        && auction.getStatus() != AuctionStatus.CANCELED
        && auction.getStatus() != AuctionStatus.FAILED
    ){
      throw new ErrorException(ErrorCode.AUCTION_CANNOT_DELETE);
    }
  }

  public AuctionDetailResponse getAuctionDetail(Long auctionId, Long userId) {

    Auction auction = getAuctionById(auctionId);

    Long displayPrice = auction.getDisplayPrice();

    Double discountRate = null;
    if(auction.getStatus() != AuctionStatus.SCHEDULED) {
      discountRate = auction.calculateDiscountRate();
    }

    boolean isSeller = auction.isSeller(userId);

    Long exposedStopLoss = null;
    if (isSeller) {
      exposedStopLoss = auction.getStopLoss();
    }

    // TODO: 타 도메인 의존성 처리 (User, Like)
    // TODO: websocket 실시간 조회수 처리, 가격 하락 처리

    long viewerCount = updateAndViewerCount(auctionId, userId);

    List<AuctionHistoryResponse> historyList = getRecentHistories(auctionId);

    return AuctionDetailResponse.of(
        auction,
        displayPrice,
        discountRate,
        exposedStopLoss,
        false,
        viewerCount,
        historyList
    );
  }

  private long updateAndViewerCount(Long auctionId, Long userId) {
    String redisKey = "auction:" + auctionId + ":viewers";

    if(userId != null) {
      redisTemplate.opsForSet().add(redisKey, String.valueOf(userId));

      // 웹소켓 붙이기 전이므로 임시로 TTL 1분 설정
      redisTemplate.expire(redisKey, java.time.Duration.ofMinutes(1));
    }

    Long viewerCount = redisTemplate.opsForSet().size(redisKey);

    if (viewerCount == null) {
      return 0L;
    }
    return viewerCount;
  }

  private List<AuctionHistoryResponse> getRecentHistories(Long auctionId) {

    return historyRepository.findTop5ByAuction_IdOrderByCreateDateDesc(auctionId)
        .stream()
        .map(AuctionHistoryResponse::from)
        .toList();
  }

  private void validateIsSeller(Auction auction, User user) {
    if (user != auction.getSeller()) {
      throw new ErrorException(ErrorCode.INVALID_AUCTION_SELLER);
    }
  }
  public Auction getAuctionById(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));
  }
}
