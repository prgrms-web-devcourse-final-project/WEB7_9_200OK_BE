package com.windfall.domain.auction.entity;

import com.windfall.domain.auction.enums.ImageStatus;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionImage extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id")
  private Auction auction;

  @Column(nullable = false)
  private String image;

  @Column(nullable = false)
  private Long size;

  @Enumerated(EnumType.STRING)
  private ImageStatus status;

  public static AuctionImage create(String image, Long size,ImageStatus status) {
    return AuctionImage.builder()
        .image(image)
        .size(size)
        .status(status)
        .build();
  }
}
