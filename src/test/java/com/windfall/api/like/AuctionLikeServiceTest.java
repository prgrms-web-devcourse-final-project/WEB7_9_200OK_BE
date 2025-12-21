package com.windfall.api.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.windfall.api.like.dto.response.AuctionLikeResponse;
import com.windfall.api.like.service.AuctionLikeService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.like.entity.AuctionLike;
import com.windfall.domain.like.repository.AuctionLikeRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuctionLikeServiceTest {

  @InjectMocks
  private AuctionLikeService auctionLikeService;

  @Mock
  private AuctionLikeRepository auctionLikeRepository;

  @Mock
  private AuctionRepository auctionRepository;

  @Mock
  private Auction auction;

  Long userId = 1L;
  Long auctionId = 1L;

  @Test
  @DisplayName("[경매찜1] 찜이 아닐 때, 경매 찜하는 경우")
  void success1() {
    // given
    when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
    when(auctionLikeRepository.findByAuctionAndUserId(auction, userId)).thenReturn(
        Optional.empty());
    when(auctionLikeRepository.countByAuction(auction)).thenReturn(1L);

    // when
    AuctionLikeResponse response = auctionLikeService.toggleLike(auctionId, userId);

    // then
    verify(auctionLikeRepository, times(1)).save(any(AuctionLike.class));
    verify(auctionLikeRepository, never()).delete(any());
    assertThat(response.like()).isTrue();
    assertThat(response.likeCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("[경매찜2] 이미 찜일 때, 경매 찜하는 경우")
  void success2() {
    // given
    AuctionLike existingLike = AuctionLike.create(auction, userId);
    when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
    when(auctionLikeRepository.findByAuctionAndUserId(auction, userId)).thenReturn(
        Optional.of(existingLike));
    when(auctionLikeRepository.countByAuction(auction)).thenReturn(0L);

    // when
    AuctionLikeResponse response = auctionLikeService.toggleLike(auctionId, userId);

    // then
    verify(auctionLikeRepository, times(1)).delete(existingLike);
    verify(auctionLikeRepository, never()).save(any());
    assertThat(response.like()).isFalse();
    assertThat(response.likeCount()).isEqualTo(0L);
  }

  @Test
  @DisplayName("[경매찜 예외1] 경매 게시물이 존재하지 않는 경우")
  void exception1() {
    // given
    when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> auctionLikeService.toggleLike(auctionId, userId))
        .isInstanceOf(ErrorException.class)
        .satisfies(e -> {
          ErrorException ex = (ErrorException) e;
          assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_AUCTION);
        });

    verify(auctionLikeRepository, never()).save(any());
    verify(auctionLikeRepository, never()).delete(any());
  }
}