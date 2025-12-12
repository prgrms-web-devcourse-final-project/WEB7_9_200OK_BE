package com.windfall.domain.review.entity;


import com.windfall.domain.trade.entity.Trade;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trade_id", nullable = false, unique = true)
  private Trade trade;

  @Column(name = "rating", nullable = false)
  private int rating;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Builder
  public Review(Trade trade, int rating, String content) {
    this.trade = trade;
    this.rating = rating;
    this.content = content;
  }

  public static Review createReview(Trade trade, int rating, String content){
    return Review.builder()
        .trade(trade)
        .rating(rating)
        .content(content)
        .build();
  }

}
