package com.windfall.domain.notification.repository;

import com.windfall.domain.notification.entity.PriceNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceNotificationRepository extends JpaRepository<PriceNotification,Long> {
}
