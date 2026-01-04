package com.windfall.api.review.service;

import com.windfall.api.review.dto.request.CreateReviewRequest;
import com.windfall.api.review.dto.request.UpdateReviewRequest;
import com.windfall.api.review.dto.response.CreateReviewResponse;
import com.windfall.api.review.dto.response.ReviewDetailsRaw;
import com.windfall.api.review.dto.response.ReviewDetailsResponse;
import com.windfall.api.review.dto.response.UpdateReviewResponse;
import com.windfall.domain.auction.entity.AuctionImage;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.domain.review.entity.Review;
import com.windfall.domain.review.repository.ReviewRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.trade.repository.TradeRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final TradeRepository tradeRepository;
  private final ReviewRepository reviewRepository;
  private final AuctionImageRepository auctionImageRepository;

  @Transactional
  public CreateReviewResponse createReview(Long userId, CreateReviewRequest request) {

    Trade trade = tradeRepository.findById(request.tradeId())
        .orElseThrow(() -> new ErrorException( //거래가 있는지
            ErrorCode.NOT_FOUND_TRADE));

    boolean reviewAlreadyExists = reviewRepository.existsReviewByTradeId(
        trade.getId()); //해당 거래에 대한 리뷰가 이미 존재하는지 (애플리케이션 단에서 추가 방지)

    if (reviewAlreadyExists) {
      throw new ErrorException(ErrorCode.REVIEW_ALREADY_EXISTS);
    }

    if (!trade.getStatus().equals(TradeStatus.PURCHASE_CONFIRMED)) {
      throw new ErrorException(ErrorCode.NOT_PURCHASE_CONFIRMED);//구매 확정 거래가 아닌 경우
    }

    if (!trade.getBuyerId().equals(userId)) { //유저 id와 buyer id가 불일치할 경우
      throw new ErrorException(ErrorCode.NOT_MATCHED_BUYER_ID);
    }

    Review review = Review.createReview(trade, request.rating(), request.content());
    reviewRepository.save(review);

    return CreateReviewResponse.from(review);
  }

  @Transactional
  public UpdateReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ErrorException( //리뷰가 있는지
            ErrorCode.NOT_FOUND_REVIEW));

    isMatchedBuyerID(review, userId); //유저 id와 buyer id가 불일치할 경우

    review.updateReview(request.rating(), request.content());

    return UpdateReviewResponse.from(review);
  }

  @Transactional
  public void deleteReview(Long userId, Long reviewId) {

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ErrorException( //리뷰가 있는지
            ErrorCode.NOT_FOUND_REVIEW));

    isMatchedBuyerID(review, userId); //유저 id와 buyer id가 불일치할 경우

    reviewRepository.delete(review);
  }

  @Transactional(readOnly = true)
  public ReviewDetailsResponse readReviewDetails(Long reviewId){
    reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ErrorException( //리뷰가 있는지
            ErrorCode.NOT_FOUND_REVIEW));

    ReviewDetailsRaw rawReview = reviewRepository.findReviewDetails(reviewId); //이미지를 제외한 리뷰 정보 프로젝션

    Long auctionId = rawReview.auctionId();

    AuctionImage auctionImage = auctionImageRepository.findTop1ByAuctionIdOrderByIdAsc(auctionId).orElse(null); //썸네일 이미지 출력

    return ReviewDetailsResponse.of(rawReview, auctionImage.getImage());

  }

  private void isMatchedBuyerID(Review review, Long userId){ //유저 id와 buyer id가 불일치할 경우
    if (!review.getTrade().getBuyerId().equals(userId)) {
      throw new ErrorException(ErrorCode.NOT_MATCHED_BUYER_ID);
    }
  }

}
