package com.windfall.api.auction.service;

import static com.windfall.domain.auction.enums.AuctionStatus.PROCESS;
import static com.windfall.domain.auction.enums.AuctionStatus.SCHEDULED;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionSchedulerService {

  private final AuctionRepository auctionRepository;
  private final AuctionStateService auctionStateService;

  public void openScheduledAuctions(LocalDateTime now) {
    List<Auction> startingAuctions = auctionRepository.findAllByStatusAndStartedAtLessThanEqual(
        SCHEDULED, now
    );

    for (Auction auction : startingAuctions) {
      try{
        auctionStateService.startAuction(auction.getId());
      } catch (Exception e) {
        log.error("경매 시작 처리 실패 ( 경매 ID: {} )", auction.getId(), e);
      }
    }
  }

  public void dropAuctionPrices(LocalDateTime now) {
    List<Auction> activeAuctions = auctionRepository.findAllByStatus(PROCESS);

    for (Auction auction : activeAuctions) {
      try {
        auctionStateService.decreasePrice(auction.getId(), now);
      } catch (Exception e) {
        log.error("경매 가격 하락 처리 실패 ( 경매 ID: {} )", auction.getId(), e);
      }
    }
  }
}
