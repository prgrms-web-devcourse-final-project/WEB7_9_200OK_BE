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
        setTarget(notification.getType()),
        notification.getTargetId()
    );
  }

  private static String setTarget(NotificationType type){
    if(type == NotificationType.CHAT_MESSAGE){
      return "chatRoom";
    }
    if (type == NotificationType.REVIEW_REGISTERED){
      return "review";
    }
    if(type == NotificationType.PURCHASE_CONFIRMED_SELLER){
      return "payment";
    }

    return "auction";
  }
}
