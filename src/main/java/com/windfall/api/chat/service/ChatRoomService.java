package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.dto.response.ChatRoomDetailResponse;
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
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  private final AuctionImageRepository auctionImageRepository;

  public List<ChatRoomListResponse> getChatRooms(Long userId, ChatRoomScope scope) {

    User me = userService.getUserById(userId);

    List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomForList(me.getId(), scope.name());

    if (chatRooms.isEmpty()) {
      return List.of();
    }

    List<ChatRoom> visibleRooms = chatRooms.stream()
        .filter(this::isVisibleTradeStatus)
        .toList();

    if (visibleRooms.isEmpty()) {
      return List.of();
    }

    List<Long> chatRoomIds = visibleRooms.stream().map(ChatRoom::getId).toList();
    Map<Long, Long> unreadMap = new HashMap<>();
    chatMessageRepository.countUnreadByChatRoomIds(me.getId(), chatRoomIds)
        .forEach(p -> unreadMap.put(p.getChatRoomId(), p.getCnt()));

    Set<Long> buyerPartnerIds = new HashSet<>();
    for (ChatRoom cr : visibleRooms) {
      Trade trade = cr.getTrade();
      Long partnerId = resolvePartnerId(me.getId(), trade);

      if (!partnerId.equals(trade.getSellerId())) {
        buyerPartnerIds.add(partnerId);
      }
    }

    Map<Long, User> buyerUserMap = userService.getUsersMapByIds(buyerPartnerIds);

    List<Long> auctionIds = visibleRooms.stream()
        .map(cr -> cr.getTrade().getAuction().getId())
        .distinct()
        .toList();

    Map<Long, String> auctionThumbMap = new HashMap<>();

    if (!auctionIds.isEmpty()) {
      auctionImageRepository.findFirstImagesByAuctionIds(auctionIds)
          .forEach(ai -> auctionThumbMap.put(ai.getAuction().getId(), ai.getImage()));
    }

    return visibleRooms.stream()
        .map(cr -> toResponse(me.getId(), cr, unreadMap, buyerUserMap, auctionThumbMap))
        .toList();
  }

  public ChatRoomDetailResponse getChatRoomDetail(Long userId, Long chatRoomId, Long cursor,
      int size) {

    User me = userService.getUserById(userId);

    ChatRoom chatRoom = chatRoomRepository.findDetailById(chatRoomId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_CHAT_ROOM));

    Trade trade = chatRoom.getTrade();
    Auction auction = trade.getAuction();

    boolean isBuyer = me.getId().equals(trade.getBuyerId());
    boolean isSeller = me.getId().equals(trade.getSellerId());
    if (!isBuyer && !isSeller) {
      throw new ErrorException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }


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

    User partnerUser = partnerId.equals(trade.getSellerId())
        ? auction.getSeller()
        : buyerUserMap.get(partnerId);

    PartnerInfo partnerInfo = (partnerUser != null)
        ? PartnerInfo.from(partnerUser)
        : new PartnerInfo(partnerId, "알 수 없음", null);

    String thumbUrl = auctionThumbMap.get(auction.getId());
    AuctionInfo auctionInfo = AuctionInfo.of(auction, thumbUrl);

    LastMessageInfo lastMessageInfo = LastMessageInfo.from(chatRoom);

    long unreadCount = unreadMap.getOrDefault(chatRoom.getId(), 0L);

    return ChatRoomListResponse.of(chatRoom, partnerInfo, auctionInfo, lastMessageInfo,
        unreadCount);
  }

}
