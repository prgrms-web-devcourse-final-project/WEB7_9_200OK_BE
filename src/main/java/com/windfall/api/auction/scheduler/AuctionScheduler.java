package com.windfall.api.auction.scheduler;

import com.windfall.api.auction.service.AuctionSchedulerService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

  private final AuctionSchedulerService auctionSchedulerService;

  @Scheduled(cron = "0 0/5 * * * *")
  public void runAuctionScheduler() {
    LocalDateTime now = LocalDateTime.now();
    log.info("⏱️스케줄러 실행: {}", now);

    auctionSchedulerService.openScheduledAuctions(now);

    auctionSchedulerService.dropAuctionPrices(now);
  }
}
