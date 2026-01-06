package com.windfall.api.notificationsetting;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.windfall.api.notificationsetting.dto.request.UpdateNotySettingRequest;
import com.windfall.api.notificationsetting.dto.response.ReadNotySettingResponse;
import com.windfall.api.notificationsetting.dto.response.UpdateNotySettingResponse;
import com.windfall.api.notificationsetting.service.NotificationSettingService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.notification.entity.NotificationSetting;
import com.windfall.domain.notification.enums.NotificationSettingType;
import com.windfall.domain.notification.repository.NotificationSettingRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationSettingServiceTest {

  @Mock
  private NotificationSettingRepository notificationSettingRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuctionRepository auctionRepository;

  @InjectMocks
  private NotificationSettingService service;

  @Mock
  private User user;

  @Mock
  private Auction auction;

  Long userId = 1L;
  Long auctionId = 1L;

  @Test
  @DisplayName("[알림 세팅 조회1] 알림 세팅 수정을 한 번도 안 한 상태에서 조회하는 경우")
  void success1() {
    // given
    when(notificationSettingRepository.findByUserIdAndAuctionId(userId, auctionId))
        .thenReturn(Collections.emptyList());

    // when
    ReadNotySettingResponse response = service.read(auctionId, userId);

    // then
    assertFalse(response.auctionStart());
    assertFalse(response.auctionEnd());
    assertFalse(response.priceReached());
  }

  @Test
  @DisplayName("[알림 세팅 수정1] 알림 세팅을 수정하는 경우")
  void success2() {
    // given
    UpdateNotySettingRequest request = new UpdateNotySettingRequest(true, true, false, null);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

    when(user.getId()).thenReturn(userId);
    when(auction.getId()).thenReturn(auctionId);

    when(notificationSettingRepository.save(any(NotificationSetting.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(notificationSettingRepository.findByUserIdAndAuctionIdAndType(userId, auctionId,
        NotificationSettingType.AUCTION_START))
        .thenReturn(Optional.empty());
    when(notificationSettingRepository.findByUserIdAndAuctionIdAndType(userId, auctionId,
        NotificationSettingType.AUCTION_END))
        .thenReturn(Optional.empty());
    when(notificationSettingRepository.findByUserIdAndAuctionIdAndType(userId, auctionId,
        NotificationSettingType.PRICE_REACHED))
        .thenReturn(Optional.empty());

    // when
    UpdateNotySettingResponse response = service.update(auctionId, request, userId);

    // then
    assertTrue(response.auctionStart());
    assertTrue(response.auctionEnd());
    assertFalse(response.priceReached());

    verify(notificationSettingRepository, times(3)).save(any(NotificationSetting.class));
  }
}