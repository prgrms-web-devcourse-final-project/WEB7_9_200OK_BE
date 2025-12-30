package com.windfall.api.notification.dto.response;

public record NotificationMarkAllResponse(
    Long userId,
    long updatedCount
) {
  public static NotificationMarkAllResponse of(Long userId, long updatedCount){
    return new NotificationMarkAllResponse(userId,updatedCount);
  }
}
