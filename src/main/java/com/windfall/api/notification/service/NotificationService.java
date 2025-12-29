package com.windfall.api.notification.service;

import com.windfall.api.notification.dto.response.NotificationMarkResponse;
import com.windfall.api.notification.dto.response.NotificationReadResponse;
import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.domain.user.entity.CustomUserDetails;

import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;


  public SliceResponse<NotificationReadResponse> readNotification(CustomUserDetails user, Pageable pageable) {
    Slice<Notification> notifications = notificationRepository.findByUserId(user.getUserId(), pageable);
    Slice<NotificationReadResponse> responseSlice = notifications.map(NotificationReadResponse::from);
    return SliceResponse.from(responseSlice);
  }

  @Transactional
  public NotificationMarkResponse markAsRead(Long notificationId, CustomUserDetails user) {
    Notification notification = getNotification(notificationId);

    notification.updateReadStatus(true);


    return NotificationMarkResponse.from(notification);
  }

  private Notification getNotification(Long notificationId){
    return notificationRepository.findById(notificationId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_NOTIFICATION));
  }
}
