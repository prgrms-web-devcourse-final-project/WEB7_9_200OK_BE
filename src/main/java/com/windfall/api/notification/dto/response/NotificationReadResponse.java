package com.windfall.api.notification.dto.response;

import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;
import java.time.LocalDateTime;

public record NotificationReadResponse(
    NotificationType type,
    String title,
    String message,
    Boolean readStatus,
    Long targetId,
    LocalDateTime notificationAt
) {
  public static NotificationReadResponse from(Notification notification){
    return new NotificationReadResponse(
        notification.getType(),
        notification.getTitle(),
        notification.getMessage(),
        notification.getReadStatus(),
        notification.getTargetId(),
        notification.getCreateDate()
    );
  }
}
