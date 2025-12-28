package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.dto.response.ChatRoomDetailResponse;
import com.windfall.api.chat.dto.response.ChatRoomListResponse;
import com.windfall.api.chat.dto.response.info.AuctionInfo;
import com.windfall.api.chat.dto.response.info.ChatMessageInfo;
import com.windfall.api.chat.dto.response.info.ChatRoomMetaInfo;
import com.windfall.api.chat.dto.response.info.LastMessageInfo;
import com.windfall.api.chat.dto.response.info.PartnerInfo;
import com.windfall.api.chat.dto.response.info.TradeInfo;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.entity.AuctionImage;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.domain.chat.entity.ChatMessage;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.enums.ChatMessageType;
import com.windfall.domain.chat.repository.ChatImageRepository;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import com.windfall.global.response.CursorResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final UserService userService;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatImageRepository chatImageRepository;
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

    // 1) 채팅방 + trade + auction + seller 로드
    ChatRoom chatRoom = chatRoomRepository.findDetailById(chatRoomId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_CHAT_ROOM));

    Trade trade = chatRoom.getTrade();
    Auction auction = trade.getAuction();

    // 2) 참여자 검증
    boolean isBuyer = me.getId().equals(trade.getBuyerId());
    boolean isSeller = me.getId().equals(trade.getSellerId());
    if (!isBuyer && !isSeller) {
      throw new ErrorException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }

    // 3) 거래 상태 검증
    if (!isVisibleTradeStatus(chatRoom)) {
      throw new ErrorException(ErrorCode.INVALID_TRADE_STATUS_FOR_CHAT);
    }

    // 4) 상대방 정보 구성
    Long partnerId = isBuyer ? trade.getSellerId() : trade.getBuyerId();
    User partnerUser = partnerId.equals(trade.getSellerId())
        ? auction.getSeller() : userService.getUserById(partnerId);

    PartnerInfo partnerInfo = PartnerInfo.from(partnerUser);

    // 5) 경매 썸네일(대표 이미지 1장)
    String thumbUrl = auctionImageRepository.findTop1ByAuctionIdOrderByIdAsc(auction.getId())
        .map(AuctionImage::getImage)
        .orElse(null);

    // 6) AuctionInfo, TradeInfo, ChatRoomMetaInfo 구성
    AuctionInfo auctionInfo = AuctionInfo.of(auction, thumbUrl);

    TradeInfo tradeInfo = TradeInfo.of(trade.getId(), trade.getFinalPrice(), trade.getModifyDate());

    ChatRoomMetaInfo meta = ChatRoomMetaInfo.of(chatRoom.getId(), auctionInfo, partnerInfo,
        tradeInfo);

    // 7) 메시지 목록 cursor 페이징 조회
    CursorResponse<ChatMessageInfo> messages = fetchMessageCursorPage(chatRoomId, userId, cursor, size);

    return ChatRoomDetailResponse.of(meta, messages);
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

  private CursorResponse<ChatMessageInfo> fetchMessageCursorPage(Long chatRoomId, Long userId, Long cursor, int size) {

    int pageSize = Math.max(1, Math.min(size, 50));
    Pageable pageable = PageRequest.of(0, pageSize + 1); // hasNext 판단용 +1

    List<ChatMessage> fetched = (cursor == null)
        ? chatMessageRepository.findLatest(chatRoomId, pageable)
        : chatMessageRepository.findOlderThan(chatRoomId, cursor, pageable);

    boolean hasNext = fetched.size() > pageSize;
    if (hasNext) fetched = fetched.subList(0, pageSize);

    // 최신->과거로 가져왔으니 응답은 과거->최신으로
    Collections.reverse(fetched);

    // IMAGE 메시지에 대해서만 ChatImage 조회
    List<Long> imageMessageIds = fetched.stream()
        .filter(m -> m.getMessageType() == ChatMessageType.IMAGE)
        .map(ChatMessage::getId)
        .toList();

    Map<Long, List<String>> imageUrlMap = new HashMap<>();
    if (!imageMessageIds.isEmpty()) {
      chatImageRepository.findByMessageIds(imageMessageIds).forEach(ci -> {
        Long msgId = ci.getChatMessage().getId();
        imageUrlMap.computeIfAbsent(msgId, k -> new ArrayList<>()).add(ci.getImageUrl());
      });
    }

    // DTO 변환
    List<ChatMessageInfo> messageInfos = fetched.stream()
        .map(cm -> new ChatMessageInfo(
            cm.getId(),
            cm.getSender().getId(),
            cm.getSender().getId().equals(userId),
            cm.getMessageType(),
            cm.getContent(),
            imageUrlMap.getOrDefault(cm.getId(), List.of()),
            cm.isRead(),
            cm.getCreateDate()
        ))
        .toList();

    // nextCursor = 이번 응답에서 가장 오래된 메시지의 ID
    Long nextCursor = null;
    if (hasNext && !messageInfos.isEmpty()) {
      nextCursor = messageInfos.get(0).messageId(); // reverse 했으니 0번이 가장 과거
    }

    return CursorResponse.of(messageInfos, nextCursor, hasNext, pageSize);
  }
}
