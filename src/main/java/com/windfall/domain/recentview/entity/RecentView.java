package com.windfall.domain.recentview.entity;


import com.windfall.domain.auction.entity.Auction;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "recent_view",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_recent_view_auction_user",
            columnNames = {"auction_id", "user_id"}
        )
    }
)
public class RecentView extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auction_id", nullable = false)
  private Auction auction;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "viewed_at", nullable = false)
  private LocalDateTime viewedAt;

  @Builder
  public RecentView(Auction auction, Long userId, LocalDateTime viewedAt) {
    this.auction = auction;
    this.userId = userId;
    this.viewedAt = viewedAt;
  }

  public static RecentView create(Auction auction, Long userId, LocalDateTime viewedAt){
    return RecentView.builder()
        .auction(auction)
        .userId(userId)
        .viewedAt(viewedAt)
        .build();
  }

  public void updateView(){
    this.viewedAt = LocalDateTime.now();
  }
}
