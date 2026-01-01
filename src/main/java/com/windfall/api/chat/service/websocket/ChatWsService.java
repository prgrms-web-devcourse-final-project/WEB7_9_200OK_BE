package com.windfall.api.chat.service.websocket;

import com.windfall.api.chat.dto.websocket.ChatMessageEvent;
import com.windfall.api.chat.dto.websocket.ChatRoomUpdateEvent;
import com.windfall.api.chat.dto.websocket.ChatSendRequest;
import com.windfall.api.chat.service.ChatRoomService;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.chat.entity.ChatImage;
import com.windfall.domain.chat.entity.ChatMessage;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.enums.ChatMessageType;
import com.windfall.domain.chat.repository.ChatImageRepository;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.user.entity.User;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatWsService {

  private final SimpMessagingTemplate messagingTemplate;

  private final UserService userService;
  private final ChatRoomService chatRoomService;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatImageRepository chatImageRepository;

  public void sendMessage(Long userId, ChatSendRequest request) {
    User sender = userService.getUserById(userId);

    ChatRoom chatRoom = chatRoomService.getChatRoomOrThrow(request.chatRoomId());

    Trade trade = chatRoom.getTrade();
    validateParticipant(trade, userId);
    validateTradeStatus(chatRoom);

    // 메시지 저장
    ChatMessage msg = ChatMessage.create(chatRoom, sender, buildContent(request),
        request.chatMessageType());
    chatMessageRepository.save(msg);

    // IMAGE면 ChatImage 저장
    List<String> imageUrls = List.of();
    if (request.chatMessageType() == ChatMessageType.IMAGE) {
      imageUrls = (request.imageUrls() == null) ? List.of() : request.imageUrls();
      for (String url : imageUrls) {
        chatImageRepository.save(ChatImage.builder().chatMessage(msg).imageUrl(url).build());
      }
    }

    // ChatRoom lastMessage 갱신
    String preview = makePreview(request);
    chatRoom.updateLastMessage(msg.getCreateDate(), preview, request.chatMessageType());

    // 채팅방 브로드캐스트 이벤트
    ChatMessageEvent event = ChatMessageEvent.of(chatRoom.getId(), msg.getId(), sender.getId(),
        request.chatMessageType(), msg.getContent(), imageUrls, false, msg.getCreateDate());
    messagingTemplate.convertAndSend(topicRoom(chatRoom.getId()), event);

    // 목록 업데이트 이벤트 (개인 큐)
    Long buyerId = trade.getBuyerId();
    Long sellerId = trade.getSellerId();

    // 발신자: unread 변화 없음, 마지막 메시지 갱신
    sendRoomUpdateToUser(sender.getId(), chatRoom, 0, false);

    // 수신자: unread +1, 마지막 메시지 갱신
    Long receiverId = sender.getId().equals(buyerId) ? sellerId : buyerId;
    sendRoomUpdateToUser(receiverId, chatRoom, +1, false);
  }

  public void markAsRead(Long userId, Long chatRoomId) {
    userService.getUserById(userId);

    ChatRoom room = chatRoomService.getChatRoomWithTrade(chatRoomId);

    validateParticipant(room.getTrade(), userId);

    int updated = chatMessageRepository.markAllAsReadExcludingSender(chatRoomId, userId);

    // 본인 목록: unread 0으로 리셋 이벤트
    sendRoomUpdateToUser(userId, room, 0, true);

    // (선택) 상대에게 “상대가 읽음 처리했다” 이벤트 보내기
    // updated > 0일 때만 보내도 됨
    // messagingTemplate.convertAndSend(topicRoomRead(chatRoomId),
    //     new ChatReadEvent(chatRoomId, userId, LocalDateTime.now()));
  }

  private void sendRoomUpdateToUser(Long targetUserId, ChatRoom room, long unreadDelta, boolean resetUnread) {
    ChatRoomUpdateEvent update = ChatRoomUpdateEvent.of(room.getId(), room.getLastMessagePreview(),
        room.getLastMessageType(), room.getLastMessageAt(), unreadDelta, resetUnread);

    messagingTemplate.convertAndSendToUser(targetUserId.toString(), "/queue/chat.rooms", update);
  }

  private void validateParticipant(Trade trade, Long userId) {
    if (!userId.equals(trade.getBuyerId()) && !userId.equals(trade.getSellerId())) {
      throw new ErrorException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }
  }

  private void validateTradeStatus(ChatRoom room) {
    TradeStatus status = room.getTrade().getStatus();
    if (!(status == TradeStatus.PAYMENT_COMPLETED || status == TradeStatus.PURCHASE_CONFIRMED)) {
      throw new ErrorException(ErrorCode.INVALID_TRADE_STATUS_FOR_CHAT);
    }
  }

  private String buildContent(ChatSendRequest request) {
    if (request.chatMessageType() == ChatMessageType.TEXT) {
      return request.content();
    }
    return "사진을 보냈습니다.";
  }

  private String makePreview(ChatSendRequest request) {
    if (request.chatMessageType() == ChatMessageType.TEXT) {
      String c = request.content() == null ? "" : request.content();
      return c.length() > 200 ? c.substring(0, 200) : c;
    }
    return "사진을 보냈습니다.";
  }

  private String topicRoom(Long chatRoomId) {
    return "/topic/chat.rooms." + chatRoomId;
  }

}
