package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.api.auction.dto.response.AuctionCreateResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionStatus;
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

    Auction auction = Auction.create(request, seller);

    Auction savedAuction = auctionRepository.save(auction);

    return AuctionCreateResponse.from(savedAuction, seller.getId());
  }

  private void validateAuctionRequest(AuctionCreateRequest request) {
    if (request.startPrice() * 0.9 < request.stopLoss()) {
      throw new ErrorException(ErrorCode.INVALID_STOP_LOSS);
    }

    if (request.startPrice() > request.dropAmount() * 200) {
      throw new ErrorException(ErrorCode.INVALID_DROP_AMOUNT);
    }

    if (request.startAt().isBefore(LocalDateTime.now().plusDays(1L))
        || request.startAt().isAfter(LocalDateTime.now().plusDays(7L))) {
      throw new ErrorException(ErrorCode.INVALID_TIME);
    }
  }

  @Transactional
  public void deleteAuction(Long auctionId, Long userId) {
    User user = userService.getUserById(userId);

    Auction auction = getAuctionById(auctionId);

    validateDeleteAuction(auction, user);

    auctionRepository.deleteById(auctionId);
  }

  private void validateDeleteAuction(Auction auction, User user) {
    if (user != auction.getSeller()) {
      throw new ErrorException(ErrorCode.INVALID_AUCTION_SELLER);
    }

    // TODO 추가 필요: trade가 생길 시 trade 구매완료 상태값 받아오기
    if(auction.getStatus() != AuctionStatus.SCHEDULED
        && auction.getStatus() != AuctionStatus.CANCELED
        && auction.getStatus() != AuctionStatus.FAILED
    ){
      throw new ErrorException(ErrorCode.AUCTION_CANNOT_DELETE);
    }
  }
  public Auction getAuctionById(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));
  }
}
