package com.windfall.domain.notification.repository;

import com.windfall.domain.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting,Long> {
}
