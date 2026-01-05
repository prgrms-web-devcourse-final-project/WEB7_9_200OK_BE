package com.windfall.api.notificationsetting.dto.response;

import com.windfall.api.notificationsetting.dto.request.UpdateNotySettingRequest;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 세팅 수정 응답 DTO")
public record UpdateNotySettingResponse (

    @Schema(description = "경매 시작 설정 여부")
    boolean auctionStart,

    @Schema(description = "경매 종료 설정 여부")
    boolean auctionEnd,

    @Schema(description = "가격 도달 설정 여부")
    boolean priceReached,

    @Schema(description = "가격 도달 기준값")
    Long price
) {

  public static UpdateNotySettingResponse from(UpdateNotySettingRequest request) {
      return new UpdateNotySettingResponse(
          request.auctionStart(),
          request.auctionEnd(),
          request.priceReached(),
          request.price()
      );
  }
}