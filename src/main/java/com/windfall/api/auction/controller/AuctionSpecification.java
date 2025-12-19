package com.windfall.api.auction.controller;

import static com.windfall.global.exception.ErrorCode.AUCTION_CANNOT_CANCEL;
import static com.windfall.global.exception.ErrorCode.AUCTION_CANNOT_DELETE;
import static com.windfall.global.exception.ErrorCode.INVALID_AUCTION_SELLER;
import static com.windfall.global.exception.ErrorCode.INVALID_DROP_AMOUNT;
import static com.windfall.global.exception.ErrorCode.INVALID_STOP_LOSS;
import static com.windfall.global.exception.ErrorCode.INVALID_TIME;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_AUCTION;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_USER;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.request.SellerEmojiRequest;
import com.windfall.api.auction.dto.response.AuctionCancelResponse;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.auction.dto.response.AuctionDetailResponse;
import com.windfall.api.auction.dto.response.AuctionHistoryResponse;
import com.windfall.api.auction.dto.response.AuctionListReadResponse;
import com.windfall.global.config.swagger.ApiErrorCodes;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Auction", description = "경매 상세 및 상호작용 API")
public interface AuctionSpecification {


  @ApiErrorCodes({INVALID_TIME, INVALID_STOP_LOSS, INVALID_DROP_AMOUNT})
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

  @ApiErrorCodes({NOT_FOUND_AUCTION})
  @PostMapping("/auctions/{auctionId}/emoji")
  @Operation(
      summary = "경매 이모지 전송 (WebSocket)",
      description =
      """
        **웹소켓 명세를 위한 가짜 PostMapping으로 실제로는 MessageMapping으로 구현됩니다. HTTP 요청이 아닙니다.**
        
        판매자가 이모지를 전송합니다. (반환값 없음, /topic/auction/{id} 구독자에게 브로드캐스팅)
        
        연결 정보
        * **Pub:** `/app/auctions/{auctionId}/emoji`
        * **Sub:** `/topic/auction/{auctionId}`

        에러 처리
        * `NOT_FOUND_AUCTION` 에러 발생 시, 별도 응답 없습니다.
        """
  )
  public void sendEmoji(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @DestinationVariable Long auctionId,

      @Parameter(description = "이모지 요청 DTO", required = true, example = "LIKE")
      @Payload SellerEmojiRequest request,

      @Parameter(description = "임시 사용자 ID", required = true, example = "1")
      @Header(value = "userId", defaultValue = "1") Long userId
  );

  @ApiErrorCodes({NOT_FOUND_AUCTION, NOT_FOUND_USER, AUCTION_CANNOT_DELETE, INVALID_AUCTION_SELLER,
      AUCTION_CANNOT_CANCEL})
  @Operation(summary = "경매 취소", description = "경매를 취소합니다.")
  ApiResponse<AuctionCancelResponse> cancelAuction(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestParam Long userId
  );

  @ApiErrorCodes({NOT_FOUND_AUCTION, NOT_FOUND_USER, AUCTION_CANNOT_DELETE, INVALID_AUCTION_SELLER,
      AUCTION_CANNOT_DELETE})
  @Operation(summary = "경매 삭제", description = "경매를 삭제합니다.")
  ApiResponse<Void> deleteAuction(
      @Parameter(description = "경매 ID", required = true, example = "1")
      @PathVariable Long auctionId,

      @Parameter(description = "사용자 ID", required = true, example = "1")
      @RequestParam Long userId
  );
}
