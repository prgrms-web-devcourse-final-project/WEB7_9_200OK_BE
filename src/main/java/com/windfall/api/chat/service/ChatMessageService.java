package com.windfall.api.chat.service;

import com.windfall.api.chat.dto.response.ChatReadMarkResponse;
import com.windfall.api.user.service.UserService;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.user.entity.User;
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

    User me = userService.getUserById(userId);

    ChatRoom chatRoom = chatRoomRepository.findByIdWithTrade(chatRoomId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_CHAT_ROOM));

    validateParticipant(chatRoom, userId);

    int updated = chatMessageRepository.markAllAsReadExcludingSender(chatRoomId, userId);
    return new ChatReadMarkResponse(updated);
  }

  private void validateParticipant(ChatRoom chatRoom, Long userId) {
    Trade trade = chatRoom.getTrade();
    boolean isParticipant = userId.equals(trade.getBuyerId()) || userId.equals(trade.getSellerId());

    if (!isParticipant) {
      throw new ErrorException(ErrorCode.FORBIDDEN_CHAT_ROOM);
    }
  }

}
