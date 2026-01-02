package com.windfall.api.auction.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.request.SellerEmojiRequest;
import com.windfall.api.auction.dto.response.AuctionCancelResponse;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.auction.dto.response.AuctionListReadResponse;
import com.windfall.api.auction.dto.response.AuctionSearchResponse;
import com.windfall.api.auction.service.AuctionInteractionService;
import com.windfall.api.auction.service.AuctionService;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.user.entity.CustomUserDetails;
import com.windfall.global.response.ApiResponse;
import com.windfall.global.response.SliceResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController implements AuctionSpecification {

  private final AuctionService auctionService;
  private final AuctionInteractionService interactionService;

  @Override
  @PostMapping
  public ApiResponse<AuctionCreateResponse> createAuction(
      @Valid @RequestBody AuctionCreateRequest request
  ) {
    AuctionCreateResponse response = auctionService.createAuction(request);
    return ApiResponse.created("경매가 생성되었습니다.", response);
  }

  @Override
  @GetMapping("/search")
  public ApiResponse<SliceResponse<AuctionSearchResponse>> searchAuction(
      @RequestParam(defaultValue = "") String query,
      @RequestParam(required = false) AuctionCategory category,
      @RequestParam(required = false) AuctionStatus status,
      @RequestParam(defaultValue = "0") Long minPrice,
      @RequestParam(required = false) Long maxPrice,
      @RequestParam @Min(value = 1, message = "page는 1부터 시작합니다.") int page,
      @RequestParam(defaultValue = "15") int size,
      @RequestParam(defaultValue = "createDate") String sortBy,
      @RequestParam(defaultValue = "ASC") Direction sortDirection,
      @AuthenticationPrincipal CustomUserDetails user
      // TODO 태그 관련 + 필터링(최신순, 인기순, 오래된 순 등등)
  ) {
    Long userId = null;
    if (user != null) {
      userId = user.getUserId();
    }

    PageRequest pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    SliceResponse<AuctionSearchResponse> response = auctionService.searchAuction(pageable, query,
        category, status, minPrice, maxPrice, userId);
    return ApiResponse.ok("경매 검색에 성공했습니다.", response);
  }

  @Override
  @GetMapping
  public ApiResponse<AuctionListReadResponse> readAuctionList(
      @AuthenticationPrincipal CustomUserDetails user
  ) {
    Long userId = null;
    if (user != null) {
      userId = user.getUserId();
    }

    AuctionListReadResponse response = auctionService.readAuctionList(userId);
    return ApiResponse.ok("경매 목록 조회에 성공했습니다.", response);
  }

  @Override
  @GetMapping("/{auctionId}")
  public ApiResponse<AuctionDetailResponse> getAuctionDetail(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = null;
    if (userDetails != null) {
      userId = userDetails.getUserId();
    }

    AuctionDetailResponse response = auctionService.getAuctionDetail(auctionId, userId);

    return ApiResponse.ok("경매 상세 정보가 조회되었습니다.", response);
  }

  @Override
  @GetMapping("/{auctionId}/history")
  public ApiResponse<SliceResponse<AuctionHistoryResponse>> getAuctionHistory(
      @PathVariable Long auctionId,
      @PageableDefault(size = 10, sort = "createDate", direction = DESC) Pageable pageable) {

    SliceResponse<AuctionHistoryResponse> response = auctionService.getAuctionHistory(
        auctionId, pageable);

    return ApiResponse.ok("경매 가격 변동 내역이 조회되었습니다.", response);
  }

  @MessageMapping("/auctions/{auctionId}/emoji")
  public void sendEmoji(
      @DestinationVariable Long auctionId,
      @Payload SellerEmojiRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    interactionService.broadcastSellerEmoji(auctionId, userId, request.emojiType());
  }

  @Override
  @PatchMapping("/{auctionId}")
  public ApiResponse<AuctionCancelResponse> cancelAuction(
      @PathVariable Long auctionId,

      @RequestParam Long userId
  ) {
    AuctionCancelResponse response = auctionService.cancelAuction(auctionId, userId);
    return ApiResponse.ok("경매가 취소되었습니다.", response);
  }

  @Override
  @DeleteMapping("/{auctionId}")
  public ApiResponse<Void> deleteAuction(
      @PathVariable Long auctionId,

      // TODO 임시 유저 id -> 로그인 개발 시 제거해야 함
      @RequestParam Long userId
  ) {
    auctionService.deleteAuction(auctionId, userId);
    return ApiResponse.noContent();
  }

}
