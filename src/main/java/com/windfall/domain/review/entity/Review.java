package com.windfall.domain.review.entity;


import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

  //@OneToOne - Trade 엔티티 생성 이후 매핑 예정

  @Column(name = "buyer_id", nullable = false)
  private Long buyerId;

  @Column(name = "seller_id", nullable = false)
  private Long sellerId;

  @Column(name = "rating", nullable = false)
  private int rating;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;
}
