package com.windfall.api.chat.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 상세 메타 정보 DTO")
public record ChatRoomMetaInfo(

    @Schema(description = "채팅방 ID")
    Long chatRoomId,

    @Schema(description = "경매 상품 정보")
    AuctionInfo auction,

    @Schema(description = "상대방 정보")
    PartnerInfo partner,

    @Schema(description = "거래 정보")
    TradeInfo trade

) {

  public static ChatRoomMetaInfo of(Long chatRoomId, AuctionInfo auction, PartnerInfo partner,
      TradeInfo trade) {
    return new ChatRoomMetaInfo(chatRoomId, auction, partner, trade);
  }
}
