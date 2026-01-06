package com.windfall.api.notificationsetting.dto.response;

import com.windfall.domain.notification.entity.NotificationSetting;
import com.windfall.domain.notification.entity.PriceNotification;
import com.windfall.domain.notification.enums.NotificationSettingType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  public static UpdateNotySettingResponse of(
      List<NotificationSetting> settings,
      PriceNotification priceNotification
  ) {
    Map<NotificationSettingType, Boolean> map =
        settings.stream()
            .collect(Collectors.toMap(
                NotificationSetting::getType,
                NotificationSetting::isActivated
            ));

    return new UpdateNotySettingResponse(
        map.getOrDefault(NotificationSettingType.AUCTION_START, false),
        map.getOrDefault(NotificationSettingType.AUCTION_END, false),
        map.getOrDefault(NotificationSettingType.PRICE_REACHED, false),
        priceNotification != null ? priceNotification.getTargetPrice() : null
    );
  }
}