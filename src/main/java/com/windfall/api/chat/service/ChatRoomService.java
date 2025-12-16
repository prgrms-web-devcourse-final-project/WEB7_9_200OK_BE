package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.request.enums.ChatRoomScope;
import com.windfall.api.chat.dto.response.ChatRoomListResponse;
import com.windfall.api.user.service.UserService;
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

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final UserService userService;

  public List<ChatRoomListResponse> getChatRooms(Long userId, ChatRoomScope scope) {

    User me = userService.getUserById(userId);

    List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomForList(userId,
        scope.getDescription());

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

    Map<Long, User> buyerUserMap = userRepository.findAllById(buyerPartnerIds).stream()
        .collect(Collectors.toMap(User::getId, u -> u));


  }

  private boolean isVisibleTradeStatus(ChatRoom cr) {
    TradeStatus status = cr.getTrade().getStatus();
    return status == TradeStatus.PAYMENT_COMPLETED || status == TradeStatus.PURCHASE_CONFIRMED;
  }

  private Long resolvePartnerId(Long userId, Trade trade) {
    return userId.equals(trade.getBuyerId()) ? trade.getSellerId() : trade.getBuyerId();
  }

}
