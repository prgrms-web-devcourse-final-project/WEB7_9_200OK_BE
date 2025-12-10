package com.windfall.api.auction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "메인 응답 DTO")
public record AuctionListReadResponse(

    @Schema(description = "실시간 인기 랭킹 목록")
    LocalDateTime serverAt,

    @Schema(description = "실시간 인기 랭킹 목록")
    List<popularInfo> popularList,

    @Schema(description = "경매 진행 중 목록")
    List<processInfo> processList,

    @Schema(description = "경매 예정 목록")
    List<scheduledInfo> scheduledList
) {

}
