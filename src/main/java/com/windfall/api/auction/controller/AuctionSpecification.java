package com.windfall.api.auction.controller;

import static com.windfall.global.exception.ErrorCode.AUCTION_NOT_PROCESS;
import static com.windfall.global.exception.ErrorCode.INVALID_AUCTION_SELLER;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_AUCTION;

import static com.windfall.global.exception.ErrorCode.NOT_FOUND_USER;
import static com.windfall.global.exception.ErrorCode.INVALID_STOP_LOSS;
import static com.windfall.global.exception.ErrorCode.INVALID_TIME;
import static com.windfall.global.exception.ErrorCode.INVALID_DROP_AMOUNT;
import static com.windfall.global.exception.ErrorCode.AUCTION_CANNOT_DELETE;
import static com.windfall.global.exception.ErrorCode.AUCTION_CANNOT_CANCEL;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCancelResponse;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.auction.dto.response.AuctionListReadResponse;
import com.windfall.domain.auction.enums.EmojiType;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auction", description = "경매 상세 및 상호작용 API")
public interface AuctionSpecification {


  @ApiErrorCodes({INVALID_TIME,INVALID_STOP_LOSS,INVALID_DROP_AMOUNT})
  @Operation(summary = "경매 생성", description = "새로운 경매를 생성합니다.")
  ApiResponse<AuctionCreateResponse> createAuction(
      @Valid @RequestBody AuctionCreateRequest request
  );


  @Operation(summary = "경매 다건 조회", description = "경매 리스트들을 조회합니다.")
  ApiResponse<AuctionListReadResponse> readAuctionList(
  );

  @ApiErrorCodes({NOT_FOUND_USER, NOT_FOUND_AUCTION})
  @Operation(summary = "경매 상세 조회", description = "특정 경매의 상세 정보(상품 정보, 가격 정보, 상태 정보)를 조회합니다.")
  ApiResponse<AuctionDetailResponse> getAuctionDetail(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId
  );

  @ApiErrorCodes({NOT_FOUND_AUCTION})
  @Operation(summary = "경매 가격 변동 내역 조회", description = "특정 경매의 가격 변동 내역을 조회합니다.")
  ApiResponse<AuctionHistoryResponse> getAuctionHistory(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId
  );

  @ApiErrorCodes({NOT_FOUND_AUCTION, NOT_FOUND_USER, AUCTION_NOT_PROCESS ,INVALID_AUCTION_SELLER})
  @Operation(summary = "경매 이모지 전송", description = "특정 경매에 대해 판매자가 이모지를 전송합니다.")
  ApiResponse<Void> sendEmoji(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "이모지 타입", required = true, example = "LIKE")
      @PathVariable EmojiType emojiType,

      @Parameter(description = "사용자 ID", required = true, example = "42")
      @RequestBody Long userId
  );

  @ApiErrorCodes({NOT_FOUND_AUCTION, NOT_FOUND_USER, AUCTION_CANNOT_DELETE ,INVALID_AUCTION_SELLER,AUCTION_CANNOT_CANCEL})
  @Operation(summary = "경매 취소", description = "경매를 취소합니다.")
  ApiResponse<AuctionCancelResponse> cancelAuction(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestParam Long userId
  );

  @ApiErrorCodes({NOT_FOUND_AUCTION, NOT_FOUND_USER, AUCTION_CANNOT_DELETE ,INVALID_AUCTION_SELLER,AUCTION_CANNOT_DELETE})
  @Operation(summary = "경매 삭제", description = "경매를 삭제합니다.")
  ApiResponse<Void> deleteAuction(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestParam Long userId
  );
}
