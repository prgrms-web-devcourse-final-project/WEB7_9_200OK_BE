package com.windfall.api.mypage.dto.notificationsetlist;

public record NotificationInfo(
    boolean alertStart,
    boolean alertEnd,
    boolean alertPrice,
    int triggerPrice
) {


}
