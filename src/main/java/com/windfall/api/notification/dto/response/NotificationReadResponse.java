package com.windfall.api.notification.dto.response;

import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;
import java.time.LocalDateTime;

public record NotificationReadResponse(
    Long notificationId,
    NotificationType type,
    String title,
    String message,
    Boolean readStatus,
    String target,
    Long targetId,
    LocalDateTime notificationAt
) {
  public static NotificationReadResponse from(Notification notification){
    return new NotificationReadResponse(
        notification.getId(),
        notification.getType(),
        notification.getTitle(),
        notification.getMessage(),
        notification.getReadStatus(),
        notification.getType().getTarget(),
        notification.getTargetId(),
        notification.getCreateDate()
    );
  }
}
