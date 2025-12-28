package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.response.ChatReadMarkResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserService userService;

  @Transactional
  public ChatReadMarkResponse markAsRead(Long chatRoomId, Long userId) {

    userService.getUserById(userId);

    ChatRoom chatRoom = getChatRoomWithTrade(chatRoomId);

    validateParticipant(chatRoom.getTrade(), userId);

    int updated = chatMessageRepository.markAllAsReadExcludingSender(chatRoomId, userId);
    return new ChatReadMarkResponse(updated);
  }

  private ChatRoom getChatRoomWithTrade(Long chatRoomId) {
    return chatRoomRepository.findByIdWithTrade(chatRoomId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_CHAT_ROOM));
  }

  private void validateParticipant(Trade trade, Long userId) {
    if (!userId.equals(trade.getBuyerId()) && !userId.equals(trade.getSellerId())) {
      throw new ErrorException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }
  }
}
