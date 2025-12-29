package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCancelResponse;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.auction.dto.response.AuctionListReadResponse;
import com.windfall.api.auction.dto.response.AuctionSearchResponse;
import com.windfall.api.auction.dto.response.info.PopularInfo;
import com.windfall.api.auction.dto.response.info.ProcessInfo;
import com.windfall.api.auction.dto.response.info.ScheduledInfo;
import com.windfall.api.like.service.AuctionLikeService;
import com.windfall.api.tag.service.TagService;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.response.SliceResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionService {

  private final AuctionRepository auctionRepository;
  private final AuctionPriceHistoryRepository historyRepository;
  private final UserService userService;
  private final AuctionViewerService viewerService;
  private final TagService tagService;
  private final AuctionTagRepository auctionTagRepository;
  private final AuctionLikeService auctionLikeService;
  private final AuctionImageService auctionImageService;

  @Transactional
  public AuctionCreateResponse createAuction(AuctionCreateRequest request) {
    User seller = userService.getUserById(request.sellerId());

    validateAuctionRequest(request);

    Auction auction = Auction.create(request, seller);

    Auction savedAuction = auctionRepository.save(auction);

    auctionImageService.attachImagesToAuction(request.imageIds(),savedAuction);

    List<String> tags = tagService.saveTagIfExist(savedAuction, request.tags());

    return AuctionCreateResponse.from(savedAuction, seller.getId(), tags);
  }

  public SliceResponse<AuctionSearchResponse> searchAuction(Pageable pageable,String query, AuctionCategory category,
      AuctionStatus status, Long minPrice, Long maxPrice) {

    validatePrice(minPrice,maxPrice);

    Slice<AuctionSearchResponse> auctionSlice = auctionRepository.searchAuction(pageable,
        query, category, status, minPrice, maxPrice);
    return SliceResponse.from(auctionSlice);
  }

  public AuctionListReadResponse readAuctionList() {
    List<ScheduledInfo> scheduleList = auctionRepository.getScheduledInfo(AuctionStatus.SCHEDULED, 15);
    List<ProcessInfo> processList = auctionRepository.getProcessInfo(AuctionStatus.PROCESS, 15);
    List<PopularInfo> popularList = auctionRepository.getPopularInfo(AuctionStatus.PROCESS, 15);

    LocalDateTime now = LocalDateTime.now();
    return AuctionListReadResponse.of(now, popularList,processList, scheduleList);
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

    tagService.deleteTag(auction);

    auctionLikeService.deleteLike(auction);

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

    long displayPrice = auction.getDisplayPrice();

    double discountRate = 0.0;
    if(auction.getStatus() != AuctionStatus.SCHEDULED) {
      discountRate = auction.calculateDiscountRate();
    }

    long exposedStopLoss = 0L;
    if (auction.isSeller(userId)) {
      exposedStopLoss = auction.getStopLoss();
    }

    long viewerCount = viewerService.getViewerCount(auctionId);

    List<AuctionHistoryResponse> historyList = getRecentHistories(auctionId);

    List<String> tags = auctionTagRepository.findAllByAuction(auction)
        .stream()
        .map(AuctionTag::getTag)
        .map(Tag::getTagName)
        .toList();

    boolean isLiked = isLiked(auctionId, userId);
    long likeCount = auctionLikeService.getLikeCount(auctionId);

    return AuctionDetailResponse.of(
        auction,
        displayPrice,
        discountRate,
        exposedStopLoss,
        isLiked,
        likeCount,
        viewerCount,
        historyList,
        tags
    );
  }

  public SliceResponse<AuctionHistoryResponse> getAuctionHistory(Long auctionId,
      Pageable pageable) {

    getAuctionById(auctionId);

    Slice<AuctionHistoryResponse> historySlice = historyRepository.findByAuction_IdOrderByCreateDateDesc(
            auctionId, pageable)
        .map(AuctionHistoryResponse::from);

    return SliceResponse.from(historySlice);
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

  private void validatePrice(Long minPrice, Long maxPrice){
    if(minPrice != null && maxPrice != null && maxPrice < minPrice){
      throw new ErrorException(ErrorCode.INVALID_PRICE);
    }
  }

  private boolean isLiked(Long auctionId, Long userId) {
    if (userId == null) {
      return false;
    }
    return auctionLikeService.getActiveLike(auctionId, userId).isPresent();
  }
}