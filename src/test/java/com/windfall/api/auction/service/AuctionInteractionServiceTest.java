package com.windfall.api.auction.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.windfall.api.auction.dto.response.message.SellerEmojiMessage;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.EmojiType;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.global.exception.ErrorException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class AuctionInteractionServiceTest {

  @InjectMocks
  private AuctionInteractionService interactionService;

  @Mock
  private AuctionRepository auctionRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @Mock
  private Auction auction;

  @Test
  @DisplayName("성공: 판매자는 이모지를 브로드캐스팅할 수 있다.")
  void broadcastSellerEmoji_Success() {
    // given
    Long auctionId = 1L;
    Long sellerId = 1L;
    EmojiType emojiType = EmojiType.FIRE;

    Auction mockAuction = Mockito.mock(Auction.class);

    given(auctionRepository.findById(auctionId)).willReturn(Optional.of(auction));
    given(auction.isSeller(sellerId)).willReturn(true);

    // when
    interactionService.broadcastSellerEmoji(auctionId, sellerId, emojiType);

    // then
    verify(messagingTemplate).convertAndSend(
        eq("/topic/auction/" + auctionId),
        any(SellerEmojiMessage.class)
    );
  }

  @Test
  @DisplayName("실패: 판매자가 아니면 이모지를 브로드캐스팅할 수 없다.")
  void broadcastSellerEmoji_Fail_NotSeller() {
    // given
    Long auctionId = 100L;
    Long buyerId = 2L; // 판매자가 아닌 유저
    EmojiType emojiType = EmojiType.LIKE;

    given(auctionRepository.findById(auctionId)).willReturn(Optional.of(auction));
    given(auction.isSeller(buyerId)).willReturn(false);

    // when
    interactionService.broadcastSellerEmoji(auctionId, buyerId, emojiType);

    // then
    verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
  }

  @Test
  @DisplayName("실패: 존재하지 않는 경매 ID인 경우 예외가 발생한다.")
  void broadcastSellerEmoji_Fail_NotFound() {
    // given
    Long invalidAuctionId = 999L;
    Long userId = 1L;

    given(auctionRepository.findById(invalidAuctionId)).willReturn(Optional.empty());

    // when & then
    assertThrows(ErrorException.class, () ->
        interactionService.broadcastSellerEmoji(invalidAuctionId, userId, EmojiType.SAD)
    );

    verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
  }
}