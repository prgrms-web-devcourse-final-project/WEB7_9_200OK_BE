package com.windfall.api.auction.controller;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.auction.service.AuctionService;
import com.windfall.domain.auction.enums.EmojiType;
import com.windfall.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController implements AuctionSpecification {

  private final AuctionService auctionService;

  @Override
  @GetMapping("/{auctionId}")
  public ApiResponse<AuctionDetailResponse> getAuctionDetail(
      @PathVariable Long auctionId,
      @RequestBody Long userId) {

    // TODO: 경매 상세 정보 조회 및 현재가 계산 로직 구현
    // TODO: 유저 찜(Like) 여부 확인
    // TODO: Redis 실시간 접속자 수 조회 및 조회수 증가

    return ApiResponse.ok(null);
  }

  @Override
  @GetMapping("/{auctionId}/history")
  public ApiResponse<AuctionHistoryResponse> getAuctionHistory(
      @PathVariable Long auctionId) {

    //TODO: 경매 가격 변동 내역 조회 로직 구현

    return ApiResponse.ok(null);
  }

  @Override
  @PostMapping("/{auctionId}/emojis/{emojiType}")
  public ApiResponse<Void> sendEmoji(
      @PathVariable Long auctionId,
      @PathVariable EmojiType emojiType,
      @RequestBody Long userId) {

    //TODO: Redis Pub/Sub으로 이모지 발생 로직 구현

    return ApiResponse.ok(null);
  }

  @Override
  @PostMapping
  public ApiResponse<AuctionCreateResponse> createAuction(
      @RequestBody AuctionCreateRequest request
  ){
    AuctionCreateResponse response = auctionService.createAuction(request);
    return ApiResponse.created("경매가 생성되었습니다.",response);
  }
}
