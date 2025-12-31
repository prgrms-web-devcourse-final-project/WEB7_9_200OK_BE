package com.windfall.api.notificationsetting.service;

import com.windfall.api.notificationsetting.dto.request.UpdateNotySettingRequest;
import com.windfall.api.notificationsetting.dto.response.ReadNotySettingResponse;
import com.windfall.api.notificationsetting.dto.response.UpdateNotySettingResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.notification.entity.NotificationSetting;
import com.windfall.domain.notification.enums.NotificationSettingType;
import com.windfall.domain.notification.repository.NotificationSettingRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

  private final NotificationSettingRepository notificationSettingRepository;
  private final UserRepository userRepository;
  private final AuctionRepository auctionRepository;

  @Transactional(readOnly = true)
  public ReadNotySettingResponse read(Long auctionId, Long userId) {
    List<NotificationSetting> settings =
        notificationSettingRepository.findByUserIdAndAuctionId(userId, auctionId);

    // row가 하나도 없으면 전부 비활성화
    if (settings.isEmpty()) {
      return ReadNotySettingResponse.allDisabled();
    }

    return ReadNotySettingResponse.from(settings);
  }

  @Transactional
  public UpdateNotySettingResponse update(Long auctionId, UpdateNotySettingRequest request,
      Long userId
  ) {
    User user = getUser(userId);
    Auction auction = getAuction(auctionId);

    upsert(user, auction, NotificationSettingType.AUCTION_START, request.auctionStart());
    upsert(user, auction, NotificationSettingType.AUCTION_END, request.auctionEnd());
    upsert(user, auction, NotificationSettingType.PRICE_REACHED, request.priceReached());

    return UpdateNotySettingResponse.from(request);
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
  }

  private Auction getAuction(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));
  }

  private void upsert(User user, Auction auction, NotificationSettingType type,
      boolean activated
  ) {
    NotificationSetting setting = notificationSettingRepository
            .findByUserIdAndAuctionIdAndType(user.getId(), auction.getId(), type)
            .orElseGet(() -> notificationSettingRepository.save(
                NotificationSetting.create(user, auction, type)
            ));

    setting.updateActivated(activated);
  }

  // 알림 발송 판단용
  @Transactional(readOnly = true)
  public boolean isEnabled(Long userId, Long auctionId, NotificationSettingType type) {
    return notificationSettingRepository
        .findByUserIdAndAuctionIdAndType(userId, auctionId, type)
        .map(NotificationSetting::isActivated)
        .orElse(false); // row 없으면 비활성화 반환
  }
}