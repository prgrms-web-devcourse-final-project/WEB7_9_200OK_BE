package com.windfall.api.notificationsetting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 세팅 수정 요청 DTO")
public record UpdateNotySettingRequest (

    @Schema(description = "경매 시작 설정 여부", example = "true")
    boolean auctionStart,

    @Schema(description = "경매 종료 설정 여부", example = "true")
    boolean auctionEnd,

    @Schema(description = "가격 도달 설정 여부", example = "true")
    boolean priceReached,

    @Schema(description = "가격 도달 기준값", example = "10000")
    Long price
){
}