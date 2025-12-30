package com.windfall.api.mypage.dto.notificationsetlist;

import com.windfall.api.chat.dto.response.info.ChatInfo;
import com.windfall.api.mypage.dto.purchasehistory.ConfirmedPurchaseHistoryResponse;
import com.windfall.api.mypage.dto.purchasehistory.PurchaseHistoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(
    subTypes = { // 자식 클래스
        CompletedNotificationSetListResponse.class,
        NotificationSetListResponse.class,
        ProcessingNotificationSetListResponse.class
    }
)
public abstract class BaseNotificationSetList {
  @Schema(description = "경매 상태")
  private final String status;

  @Schema(description = "경매 id")
  private final Long auctionId;

  @Schema(description = "경매 제목")
  private final String title;

  @Schema(description = "경매 이미지 url")
  private final String auctionImageUrl;

  @Schema(description = "경매 시작가")
  private final int startPrice;

  @Schema(description = "경매 시작일")
  private final LocalDate startedAt;

  @Schema(description = "알림 설정 정보")
  private final NotificationInfo notificationInfo;

  public static boolean isAlertActive(Long booleanNum){
    return booleanNum == 1;
  }

  public BaseNotificationSetList(String status, Long auctionId, String title,
      String auctionImageUrl,
      int startPrice, LocalDate startedAt, boolean alertStart,
      boolean alertEnd,
      boolean alertPrice,
      int triggerPrice) {
    this.status = status;
    this.auctionId = auctionId;
    this.title = title;
    this.auctionImageUrl = auctionImageUrl;
    this.startPrice = startPrice;
    this.startedAt = startedAt;
    this.notificationInfo = new NotificationInfo(alertStart, alertEnd, alertPrice, triggerPrice);
  }
}
