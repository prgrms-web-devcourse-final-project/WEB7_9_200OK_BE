package com.windfall.api.notification.dto.response;

import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;

public record NotificationMarkResponse(
    Long notificationId,
    Boolean readStatus,
    NotificationType type,
    String target,
    Long targetId
) {

  public static NotificationMarkResponse from(Notification notification) {
    return new NotificationMarkResponse(
        notification.getId(),
        notification.getReadStatus(),
        notification.getType(),
        notification.getType().getTarget(),
        notification.getTargetId()
    );
  }
}
