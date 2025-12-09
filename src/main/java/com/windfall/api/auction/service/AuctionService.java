package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionService {

  private final AuctionRepository auctionRepository;
  private final UserService userService;

  @Transactional
  public AuctionCreateResponse createAuction(AuctionCreateRequest request) {
    User seller = userService.getUserById(request.sellerId());

    validateAuctionRequest(request);

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

  private void validateAuctionRequest(AuctionCreateRequest request) {
    if(request.startPrice() * 0.9 < request.stopLoss()){
      throw new ErrorException(ErrorCode.INVALID_STOP_LOSS);
    }

    if(request.startPrice() > request.dropAmount() * 200){
      throw new ErrorException(ErrorCode.INVALID_DROP_AMOUNT);
    }

    if(request.startAt().isBefore(LocalDateTime.now().plusDays(1L))
        || request.startAt().isAfter(LocalDateTime.now().plusDays(7L))){
      throw new ErrorException(ErrorCode.INVALID_TIME);
    }
  }
}
