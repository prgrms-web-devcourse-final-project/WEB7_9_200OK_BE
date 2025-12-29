package com.windfall.api.notification.service;

import com.windfall.api.notification.dto.response.NotificationReadResponse;
import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.domain.user.entity.CustomUserDetails;

import com.windfall.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;


  public SliceResponse<NotificationReadResponse> readNotification(CustomUserDetails user, Pageable pageable) {
    Slice<Notification> notifications = notificationRepository.findByUserId(user.getUserId(), pageable);
    Slice<NotificationReadResponse> responseSlice = notifications.map(NotificationReadResponse::from);
    return SliceResponse.from(responseSlice);
  }
}
