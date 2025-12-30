package com.windfall.domain.notification.repository;

import com.windfall.domain.notification.entity.NotificationSetting;
import com.windfall.domain.notification.enums.NotificationSettingType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

  Optional<NotificationSetting> findByUserIdAndAuctionIdAndType(Long userId, Long auctionId,
      NotificationSettingType type);

  List<NotificationSetting> findByUserIdAndAuctionId(Long userId, Long auctionId);
}