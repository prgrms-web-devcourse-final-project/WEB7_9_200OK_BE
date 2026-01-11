package com.windfall.api.chat.event;

import com.windfall.api.chat.event.vo.ChatMessageCreatedEvent;
import com.windfall.api.chat.service.redis.ChatPresenceService;
import com.windfall.api.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatNotificationEventListener {

  private final ChatPresenceService chatPresenceService;
  private final SseService sseService;

  @Async("socketTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onChatMessageCreated(ChatMessageCreatedEvent event) {

    // 수신자가 해당 채팅방을 보고 있으면 알림 스킵
    if (chatPresenceService.isViewingRoom(event.receiverId(), event.chatRoomId())) {
      return;
    }

    // SSE 알림(업서트 + 스로틀) 전송
    sseService.chatNotificationSend(
        event.receiverId(),
        event.chatRoomId(),
        event.senderName(),
        event.preview()
    );
  }
}

