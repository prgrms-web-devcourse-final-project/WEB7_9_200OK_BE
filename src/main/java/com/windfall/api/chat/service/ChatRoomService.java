package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.dto.response.ChatRoomListResponse;
import com.windfall.api.chat.dto.response.info.AuctionInfo;
import com.windfall.api.chat.dto.response.info.LastMessageInfo;
import com.windfall.api.chat.dto.response.info.PartnerInfo;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final UserService userService;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final AuctionImageRepository auctionImageRepository;

  public List<ChatRoomListResponse> getChatRooms(Long userId, ChatRoomScope scope) {

    User me = userService.getUserById(userId);

    // 1) 채팅방 조회
    List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomForList(me.getId(), scope.name());

    if (chatRooms.isEmpty()) {
      return List.of();
    }

    // 2) trade 상태 필터링(검증 로직)
    List<ChatRoom> visibleRooms = chatRooms.stream()
        .filter(this::isVisibleTradeStatus)
        .toList();

    if (visibleRooms.isEmpty()) {
      return List.of();
    }

    // 3) unreadCount 집계
    List<Long> chatRoomIds = visibleRooms.stream().map(ChatRoom::getId).toList();

    Map<Long, Long> unreadMap = new HashMap<>();
    chatMessageRepository.countUnreadByChatRoomIds(me.getId(), chatRoomIds)
        .forEach(p -> unreadMap.put(p.getChatRoomId(), p.getCnt()));

    // 4) buyer 파트너 유저들 일괄 조회
    Set<Long> buyerPartnerIds = new HashSet<>();

    for (ChatRoom cr : visibleRooms) {
      Trade trade = cr.getTrade();
      Long partnerId = resolvePartnerId(me.getId(), trade);

      // 상대가 seller면 auction.seller가 이미 있으니 buyer만 모으는 로직
      if (!partnerId.equals(trade.getSellerId())) {
        buyerPartnerIds.add(partnerId);
      }
    }

    Map<Long, User> buyerUserMap;
    if (buyerPartnerIds.isEmpty()) {
      buyerUserMap = Map.of();
    }
    buyerUserMap = userRepository.findAllById(buyerPartnerIds).stream()
        .collect(Collectors.toMap(User::getId, u -> u));

    // 5) auction 대표 이미지 일괄 조회
    List<Long> auctionIds = visibleRooms.stream()
        .map(cr -> cr.getTrade().getAuction().getId())
        .distinct()
        .toList();

    Map<Long, String> auctionThumbMap = new HashMap<>();

    if (!auctionIds.isEmpty()) {
      auctionImageRepository.findFirstImagesByAuctionIds(auctionIds)
          .forEach(ai -> auctionThumbMap.put(ai.getAuction().getId(), ai.getImage()));
    }

    // 6) DTO 변환
    return visibleRooms.stream()
        .map(cr -> toResponse(me.getId(), cr, unreadMap, buyerUserMap, auctionThumbMap))
        .toList();
  }

  private boolean isVisibleTradeStatus(ChatRoom cr) {
    TradeStatus status = cr.getTrade().getStatus();
    return status == TradeStatus.PAYMENT_COMPLETED || status == TradeStatus.PURCHASE_CONFIRMED;
  }

  private Long resolvePartnerId(Long userId, Trade trade) {
    return userId.equals(trade.getBuyerId()) ? trade.getSellerId() : trade.getBuyerId();
  }

  private ChatRoomListResponse toResponse(
      Long userId,
      ChatRoom chatRoom,
      Map<Long, Long> unreadMap,
      Map<Long, User> buyerUserMap,
      Map<Long, String> auctionThumbMap
  ) {
    Trade trade = chatRoom.getTrade();
    Auction auction = trade.getAuction();

    Long partnerId = resolvePartnerId(userId, trade);

    PartnerInfo partnerInfo;
    if (partnerId.equals(trade.getSellerId())) {
      partnerInfo = PartnerInfo.from(auction.getSeller());
    } else {
      User partner = buyerUserMap.get(partnerId);
      if (partner == null) {
        partnerInfo = new PartnerInfo(partnerId, null, null);
      } else {
        partnerInfo = PartnerInfo.from(partner);
      }
    }

    String thumbUrl = auctionThumbMap.get(auction.getId());
    AuctionInfo auctionInfo = AuctionInfo.from(auction, thumbUrl);

    LastMessageInfo lastMessageInfo = LastMessageInfo.from(chatRoom);

    long unreadCount = unreadMap.getOrDefault(chatRoom.getId(), 0L);

    return ChatRoomListResponse.of(chatRoom, partnerInfo, auctionInfo, lastMessageInfo, unreadCount);
  }

}
