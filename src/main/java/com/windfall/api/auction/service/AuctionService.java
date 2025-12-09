package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

  private final AuctionRepository auctionRepository;
  private final UserService userService;

  public AuctionCreateResponse createAuction(AuctionCreateRequest request) {
    User seller = userService.getUserById(request.sellerId());

    Auction auction = Auction.builder()
        .seller(seller)
        .title(request.title())
        .description(request.description())
        .category(request.category())
        .startPrice(request.startPrice())
        .currentPrice(request.startPrice())
        .stopLoss(request.stopLoss())
        .dropAmount(request.dropAmount())
        .startedAt(request.startAt())
        .build();

    auctionRepository.save(auction);

    return AuctionCreateResponse.from(auction, seller.getId());
  }
}
