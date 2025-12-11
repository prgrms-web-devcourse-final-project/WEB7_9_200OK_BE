package com.windfall.api.auction.controller;


import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCancelResponse;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.auction.dto.response.AuctionListReadResponse;
import com.windfall.api.auction.service.AuctionService;
import com.windfall.domain.auction.enums.EmojiType;
import com.windfall.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  @Override
  @PostMapping
  public ApiResponse<AuctionCreateResponse> createAuction(
      @Valid @RequestBody AuctionCreateRequest request
  ){
    AuctionCreateResponse response = auctionService.createAuction(request);
    return ApiResponse.created("경매가 생성되었습니다.",response);
  }

  @Override
  @GetMapping
  public ApiResponse<AuctionListReadResponse> readAuctionList(
  ){
    // TODO: 추후 프론트 담당이 생길 경우 이야기
    // TODO: 현재 응답 dto로 할지? vs api를 요청 값만 다르게해서 3번 요청할지

    return ApiResponse.ok(null);
  }

  @Override
  @GetMapping("/{auctionId}")
  public ApiResponse<AuctionDetailResponse> getAuctionDetail(
      @PathVariable Long auctionId) {

    Long userId = null; // 비회원 테스트

    AuctionDetailResponse response = auctionService.getAuctionDetail(auctionId, userId);
    // TODO: 유저 찜(Like) 여부 확인
    // TODO: Redis 실시간 접속자 수 조회 및 조회수 증가

    return ApiResponse.ok("경매 상세 정보가 조회되었습니다.", response);
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
  @PatchMapping("/{auctionId}")
  public ApiResponse<AuctionCancelResponse> cancelAuction(
      @PathVariable Long auctionId,

      @RequestParam Long userId
  ){
    AuctionCancelResponse response = auctionService.cancelAuction(auctionId, userId);
    return ApiResponse.ok("경매가 취소되었습니다.", response);
  }

  @Override
  @DeleteMapping("/{auctionId}")
  public ApiResponse<Void> deleteAuction(
      @PathVariable Long auctionId,

      // TODO 임시 유저 id -> 로그인 개발 시 제거해야 함
      @RequestParam Long userId
  ){
    auctionService.deleteAuction(auctionId, userId);
    return ApiResponse.noContent();
  }

}
