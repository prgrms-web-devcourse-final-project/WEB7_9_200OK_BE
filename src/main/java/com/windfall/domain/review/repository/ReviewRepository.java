package com.windfall.domain.review.repository;

import com.windfall.api.review.dto.response.ReviewDetailsRaw;
import com.windfall.api.review.dto.response.ReviewDetailsResponse;
import com.windfall.domain.review.entity.Review;
import com.windfall.domain.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsReviewByTradeId(Long tradeId);

  @Query("""
  SELECT new com.windfall.api.review.dto.response.ReviewDetailsRaw(
    a.id, r.id, s.id, s.profileImageUrl, a.title, s.nickname, r.rating / 10.0, r.content
    )
  FROM Review r
  JOIN r.trade t
  JOIN t.auction a
  JOIN a.seller s
  WHERE r.id = :id
  """)
  ReviewDetailsRaw findReviewDetails(@Param("id") Long reviewId);
}
