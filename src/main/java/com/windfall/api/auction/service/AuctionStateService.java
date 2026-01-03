package com.windfall.api.auction.service;

import static com.windfall.domain.auction.enums.AuctionStatus.FAILED;
import static com.windfall.domain.auction.enums.AuctionStatus.PROCESS;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_AUCTION;

import com.windfall.api.auction.service.component.AuctionMessageSender;
import com.windfall.api.notification.event.vo.AuctionPriceDroppedEvent;
import com.windfall.api.notification.service.SseService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.entity.AuctionPriceHistory;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.notification.entity.NotificationSetting;
import com.windfall.domain.notification.enums.NotificationSettingType;
import com.windfall.domain.notification.repository.NotificationSettingRepository;
import com.windfall.global.exception.ErrorException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionStateService {

  private final AuctionRepository auctionRepository;
  private final AuctionPriceHistoryRepository historyRepository;
  private final AuctionViewerService viewerService;
  private final AuctionMessageSender messageSender;
  private final ApplicationEventPublisher eventPublisher;
  private final NotificationSettingRepository notificationSettingRepository;
  private final SseService sseService;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void startAuction(Long auctionId) {
    auctionRepository.findById(auctionId).ifPresent(auction -> {

      auction.start();
      messageSender.broadcastPriceUpdate(auctionId, auction.getCurrentPrice(), PROCESS);

      notifyAuctionStart(auction);

      log.info("✅경매 시작 처리 완료 ( 경매 ID: {} )", auction.getId());
    });
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void decreasePrice(Long auctionId, LocalDateTime now) {
    Auction auction = findAuctionById(auctionId);

    if (auction.getStatus() != PROCESS) return;

    long oldPrice = auction.getCurrentPrice();
    long minutesElapsed = java.time.Duration.between(auction.getStartedAt(), now).toMinutes();
    auction.declinePrice(minutesElapsed);

    if(auction.getCurrentPrice() == oldPrice && auction.getStatus() != FAILED) return;

    savePriceHistoryWithViewers(auction);

    publishPriceDroppedEvent(auction, oldPrice, now);
    messageSender.broadcastPriceUpdate(auctionId, auction.getCurrentPrice(), auction.getStatus());

    logAuctionChange(auction, oldPrice);
  }

  private void logAuctionChange(Auction auction, long oldPrice) {
    if(auction.getStatus() == FAILED) {
      log.info("❌경매 유찰 ( 경매 ID: {}, StopLoss 도달)", auction.getId());
    } else {
      log.info("⬇️경매 가격 하락 ( 경매 ID: {}, 이전 가격: {}, 현재 가격: {} )",
          auction.getId(), oldPrice, auction.getCurrentPrice());
    }
  }

  private void savePriceHistoryWithViewers(Auction auction) {
    long viewerCount = viewerService.getViewerCount(auction.getId());

    AuctionPriceHistory history = AuctionPriceHistory.create(auction, auction.getCurrentPrice(), viewerCount);
    historyRepository.save(history);
  }

  private Auction findAuctionById(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(NOT_FOUND_AUCTION));
  }

  private void publishPriceDroppedEvent(
      Auction auction,
      long oldPrice,
      LocalDateTime now
  ) {
    eventPublisher.publishEvent(
        new AuctionPriceDroppedEvent(
            auction.getId(),
            oldPrice,
            auction.getCurrentPrice(),
            now
        )
    );
  }

  private void notifyAuctionStart(Auction auction) {
    List<NotificationSetting> settings = getActiveAuctionStartSettings(auction);

    if(settings.isEmpty()) return;

    for(NotificationSetting setting : settings) {
      try {
        sseService.auctionStartNotificationSend(
            setting.getUser().getId(),
            auction.getId(),
            auction.getTitle()
        );
      } catch (Exception e) {
        log.error("알림 발송 실패 [User: {}]: {}", setting.getUser().getId(), e.getMessage());
      }
    }
  }

  private List<NotificationSetting> getActiveAuctionStartSettings(Auction auction) {
    return notificationSettingRepository.findAllActiveByAuctionAndType(
        auction.getId(),
        NotificationSettingType.AUCTION_START
    );
  }
}