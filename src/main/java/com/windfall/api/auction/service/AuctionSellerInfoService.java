package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.response.AuctionSellerInfoResponse;
import com.windfall.api.auction.dto.response.info.BuyerReviewInfo;
import com.windfall.api.auction.dto.response.info.SellerAuctionsInfo;
import com.windfall.api.auction.dto.response.raw.SellerAuctionsRaw;
import com.windfall.api.auction.dto.response.stats.SellerReviewStats;
import com.windfall.api.user.dto.response.reviewlist.AuctionImageRaw;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.auction.repository.AuctionSellerInfoRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionSellerInfoService {

  private final AuctionSellerInfoRepository auctionSellerInfoRepository;
  private final UserRepository userRepository;
  private final AuctionImageRepository auctionImageRepository;

  public AuctionSellerInfoResponse getAuctionSellerInfo(Long sellerId){
    User seller = userRepository.findById(sellerId).orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER)); //seller 검증

    SellerReviewStats reviewStats = auctionSellerInfoRepository.getSellerReviewStats(sellerId); //통계 쿼리
    List<BuyerReviewInfo> buyerReviewInfo = auctionSellerInfoRepository.getBuyerInfo(sellerId, PageRequest.of(0, 4)); //4개 뽑아오기 (구매자 리뷰 - 최신 순)
    List<SellerAuctionsRaw> sellerAuctionsRaws = auctionSellerInfoRepository.getRawSellerAuctions(sellerId, PageRequest.of(0, 10)); //10개 뽑아오기 (판매자 경매 - 최신 순)

    List<Long> auctionIds = sellerAuctionsRaws.stream().map(SellerAuctionsRaw::auctionId).toList();
    List<AuctionImageRaw> auctionImages = auctionImageRepository.findFirstImagesProjection(auctionIds); //각 경매의 첫 번째 이미지 추출

    Map<Long, String> mappingImage = auctionImages.stream().collect(Collectors.toMap(
        AuctionImageRaw::auctionId, //매핑용 이미지 설정
        AuctionImageRaw::auctionImageUrl));

    List<SellerAuctionsInfo> sellerAuctionsInfo = sellerAuctionsRaws.stream().map(data -> SellerAuctionsInfo.of(data, mappingImage.get(data.auctionId()))).toList(); //순서대로 DTO 변환

    return AuctionSellerInfoResponse.of(seller, reviewStats, buyerReviewInfo, sellerAuctionsInfo);
  }
}
