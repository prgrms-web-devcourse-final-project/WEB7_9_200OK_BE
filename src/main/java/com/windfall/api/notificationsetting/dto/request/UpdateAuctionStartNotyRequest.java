package com.windfall.api.notificationsetting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 시작 알림 단일 설정 요청 DTO")
public record UpdateAuctionStartNotyRequest (

    @Schema(description = "경매 시작 설정 여부", example = "true")
    boolean auctionStart
){
}