package com.windfall.api.auction.dto.response;

import com.windfall.api.auction.dto.response.info.PopularInfo;
import com.windfall.api.auction.dto.response.info.ProcessInfo;
import com.windfall.api.auction.dto.response.info.ScheduledInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "메인 응답 DTO")
public record AuctionListReadResponse(

    @Schema(description = "실시간 인기 랭킹 목록")
    LocalDateTime serverAt,

    @Schema(description = "실시간 인기 랭킹 목록")
    List<PopularInfo> popularList,

    @Schema(description = "경매 진행 중 목록")
    List<ProcessInfo> processList,

    @Schema(description = "경매 예정 목록")
    List<ScheduledInfo> scheduledList
) {
  public static AuctionListReadResponse of(
      LocalDateTime serverAt,
      List<PopularInfo> popularList,
      List<ProcessInfo> processList,
      List<ScheduledInfo> scheduledList
  ) {
    return new AuctionListReadResponse(
        serverAt,
        popularList,
        processList,
        scheduledList
    );
  }

}
