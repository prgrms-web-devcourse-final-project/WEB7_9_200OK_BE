package com.windfall.domain.notification.repository;

import com.windfall.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
  Slice<Notification> findByUserId(Long userId, Pageable pageable);
}
