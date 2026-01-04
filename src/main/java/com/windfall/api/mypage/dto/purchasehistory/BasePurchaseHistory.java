package com.windfall.api.mypage.dto.purchasehistory;
import com.windfall.api.chat.dto.response.info.ChatInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(
    subTypes = { // 자식 클래스
        PurchaseHistoryResponse.class,
        ConfirmedPurchaseHistoryResponse.class
    }
)
public abstract class BasePurchaseHistory {

  @Schema(description = "경매 상태")
  private final String status;

  @Schema(description = "경매 id")
  private final Long auctionId;

  @Schema(description = "거래 id")
  private final Long tradeId;

  @Schema(description = "경매 제목")
  private final String title;

  @Schema(description = "경매 이미지 url")
  private final String auctionImageUrl;

  @Schema(description = "경매 시작가")
  private final int startPrice;

  @Schema(description = "낙찰가")
  private final int endPrice;

  @Schema(description = "하락 퍼센트")
  private final int discountPercent;

  @Schema(description = "낙찰 일시")
  private final LocalDate purchasedDate;

  @Schema(description = "채팅 정보")
  private final ChatInfo chatInfo;

  public BasePurchaseHistory(String status, Long auctionId, Long tradeId,String title, String auctionImageUrl,
      int startPrice, int endPrice, int discountPercent, LocalDate purchasedDate, Long roomId, int unreadCount) {
    this.status = status;
    this.auctionId = auctionId;
    this.tradeId = tradeId;
    this.title = title;
    this.auctionImageUrl = auctionImageUrl;
    this.startPrice = startPrice;
    this.endPrice = endPrice;
    this.discountPercent = discountPercent;
    this.purchasedDate = purchasedDate;
    this.chatInfo = new ChatInfo(roomId, unreadCount);
  }
}
