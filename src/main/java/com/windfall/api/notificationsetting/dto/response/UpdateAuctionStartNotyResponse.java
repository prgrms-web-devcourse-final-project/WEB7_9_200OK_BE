package com.windfall.api.notificationsetting.dto.response;

import com.windfall.api.notificationsetting.dto.request.UpdateAuctionStartNotyRequest;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 시작 알림 단일 설정 응답 DTO")
public record UpdateAuctionStartNotyResponse (

    @Schema(description = "경매 시작 설정 여부")
    boolean auctionStart
){
  public static UpdateAuctionStartNotyResponse from(UpdateAuctionStartNotyRequest request) {
    return new UpdateAuctionStartNotyResponse(request.auctionStart());
  }
}