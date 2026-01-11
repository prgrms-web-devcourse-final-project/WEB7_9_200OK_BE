package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.response.ChatReadMarkResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final NotificationRepository notificationRepository;
  private final UserService userService;
  private final ChatRoomService chatRoomService;


  @Transactional
  public ChatReadMarkResponse markAsRead(Long chatRoomId, Long userId) {

    userService.getUserById(userId);

    ChatRoom chatRoom = chatRoomService.getChatRoomWithTrade(chatRoomId);

    validateParticipant(chatRoom.getTrade(), userId);

    int updated = chatMessageRepository.markAllAsReadExcludingSender(chatRoomId, userId);

    // 알림 읽음 동기화
    notificationRepository.markChatRoomAsRead(userId, chatRoomId);
    return ChatReadMarkResponse.of(updated);
  }

  private void validateParticipant(Trade trade, Long userId) {
    if (!userId.equals(trade.getBuyerId()) && !userId.equals(trade.getSellerId())) {
      throw new ErrorException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }
  }
}
