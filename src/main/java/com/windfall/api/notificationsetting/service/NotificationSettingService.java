package com.windfall.api.notificationsetting.service;

import com.windfall.api.notificationsetting.dto.request.UpdateAuctionStartNotyRequest;
import com.windfall.api.notificationsetting.dto.request.UpdateNotySettingRequest;
import com.windfall.api.notificationsetting.dto.response.ReadNotySettingResponse;
import com.windfall.api.notificationsetting.dto.response.UpdateAuctionStartNotyResponse;
import com.windfall.api.notificationsetting.dto.response.UpdateNotySettingResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.notification.entity.NotificationSetting;
import com.windfall.domain.notification.entity.PriceNotification;
import com.windfall.domain.notification.enums.NotificationSettingType;
import com.windfall.domain.notification.repository.NotificationSettingRepository;
import com.windfall.domain.notification.repository.PriceNotificationRepository;
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
  private final PriceNotificationRepository priceNotificationRepository;

  @Transactional(readOnly = true)
  public ReadNotySettingResponse read(Long auctionId, Long userId) {
    List<NotificationSetting> settings = getByUserIdAndAuctionId(auctionId, userId);

    // row가 하나도 없으면 전부 비활성화
    if (settings.isEmpty()) {
      return ReadNotySettingResponse.allDisabled();
    }

    PriceNotification priceNotification = getPriceNotification(auctionId, userId);

    return ReadNotySettingResponse.of(settings, priceNotification);
  }

  @Transactional
  public UpdateNotySettingResponse update(Long auctionId, UpdateNotySettingRequest request,
      Long userId
  ) {
    if (request.priceReached()) {
      validatePrice(request);
    }

    User user = getUser(userId);
    Auction auction = getAuction(auctionId);

    upsert(user, auction, NotificationSettingType.AUCTION_START, request.auctionStart());
    upsert(user, auction, NotificationSettingType.AUCTION_END, request.auctionEnd());
    upsert(user, auction, NotificationSettingType.PRICE_REACHED, request.priceReached());

    if (request.priceReached()) {
      upsertPriceNotification(userId, auctionId, request.price());
    }

    List<NotificationSetting> settings = getByUserIdAndAuctionId(auctionId, userId);
    PriceNotification priceNotification = getPriceNotification(auctionId, userId);

    return UpdateNotySettingResponse.of(settings, priceNotification);
  }

  @Transactional
  public UpdateAuctionStartNotyResponse updateAuctionStartNotification(
      Long auctionId,
      UpdateAuctionStartNotyRequest request,
      Long userId
  ) {
    User user = getUser(userId);
    Auction auction = getAuction(auctionId);

    upsert(user, auction, NotificationSettingType.AUCTION_START, request.auctionStart());

    NotificationSetting setting =
        notificationSettingRepository.findByUserIdAndAuctionIdAndType(
            userId,
            auctionId,
            NotificationSettingType.AUCTION_START
        )
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION_START_NOTY));

    return UpdateAuctionStartNotyResponse.from(setting.isActivated());
  }

  private List<NotificationSetting> getByUserIdAndAuctionId(Long auctionId, Long userId) {
    return notificationSettingRepository.findByUserIdAndAuctionId(userId, auctionId);
  }

  private PriceNotification getPriceNotification(Long auctionId, Long userId) {
    return priceNotificationRepository
        .findByUserIdAndAuctionId(userId, auctionId)
        .orElse(null);
  }

  private void validatePrice(UpdateNotySettingRequest request) {
      if (request.price() == null || request.price() <= 0) {
        throw new ErrorException(ErrorCode.INVALID_PRICE_NOTIFICATION);
      }
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

  private void upsertPriceNotification(Long userId, Long auctionId, Long price) {
    NotificationSetting priceSetting =
        notificationSettingRepository
            .findByUserIdAndAuctionIdAndType(
                userId, auctionId, NotificationSettingType.PRICE_REACHED
            )
            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_PRICE_REACHED_NOTY));

    PriceNotification pn = priceNotificationRepository
        .findByUserIdAndAuctionId(userId, auctionId)
        .orElseGet(() -> priceNotificationRepository.save(
            PriceNotification.builder()
                .setting(priceSetting)
                .userId(userId)
                .auctionId(auctionId)
                .targetPrice(price)
                .notified(false)
                .build()
        ));

    pn.updateTargetPrice(price);
    pn.resetNotified();
  }
}