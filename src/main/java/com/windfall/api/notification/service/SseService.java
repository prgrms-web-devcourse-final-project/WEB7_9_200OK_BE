package com.windfall.api.notification.service;

import com.windfall.api.notification.dto.response.NotificationReadResponse;
import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {
  private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final RedisTemplate<String, String> redisTemplate;


  // TODO 현재 lastEventId 처리는 구현하지 않음 -> 나중에 userId가 아닌 따로 관리할 때 구현 예정 + SSE 관련 캐싱이나 DB 저장도 고려
  public SseEmitter subscribe(Long userId,String lastEventId) {
    long timeout = 1000L * 60 * 60;
    String id = String.valueOf(userId);

    SseEmitter sseEmitter = new SseEmitter(timeout);
    sseEmitterMap.put(String.valueOf(id), sseEmitter);


    sseEmitter.onCompletion(() -> sseEmitterMap.remove(id));

    sseEmitter.onTimeout(sseEmitter::complete);

    sseEmitter.onError(throwable -> sseEmitter.complete());


    sendToClient(id, "","EventStream Created. [userId= %s]".formatted(userId));

    return sseEmitter;
  }


  public void sendToClient(String id, String eventName,  Object data) {
    SseEmitter sseEmitter = sseEmitterMap.get(id);

    if (sseEmitter == null) {
      return;
    }

    try {
      sseEmitter.send(
          SseEmitter
              .event()
              .id(id)
              .name(eventName)
              .data(data)
      );
    } catch (IOException e) {
      sseEmitter.complete();
      sseEmitterMap.remove(id);
    }
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void priceNotificationSend(Long userId, Long targetId, String content) {
    sendNotification(
        userId,
        targetId,
        "가격 하락 알림",
        "관심 상품의 가격이 하락했습니다: " + content,
        NotificationType.PRICE_DROP,
        "priceAlert"
    );
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void auctionStartNotificationSend(Long userId, Long targetId, String auctionTitle) {
    sendNotification(
        userId,
        targetId,
        "경매 시작 알림",
        "관심 상품 '" + auctionTitle + "'의 경매가 시작되었습니다.",
        NotificationType.AUCTION_START_WISHLIST,
        "auctionStartAlert"
    );
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void sendAuctionFailedToSeller(Long sellerId, Long auctionId, String auctionTitle) {
    sendNotification(
        sellerId,
        auctionId,
        "경매 유찰 알림",
        "'" + auctionTitle + "' 경매가 유찰되어 종료되었습니다.",
        NotificationType.AUCTION_FAILED_SELLER,
        "auctionFailedSeller"
    );
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void sendAuctionFailedToSubscriber(Long userId, Long auctionId, String auctionTitle) {
    sendNotification(
        userId,
        auctionId,
        "경매 유찰 알림",
        "'" + auctionTitle + "' 경매가 유찰되어 종료되었습니다.",
        NotificationType.AUCTION_FAILED_SUBSCRIBER,
        "auctionFailedSubscriber"
    );
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void sendAuctionSuccessToSeller(Long sellerId, Long auctionId, String auctionTitle) {
    sendNotification(
        sellerId,
        auctionId,
        "경매 낙찰 알림",
        "'" + auctionTitle + "' 경매가 낙찰되어 종료되었습니다.",
        NotificationType.SALE_SUCCESS_SELLER,
        "auctionSuccessSeller"
    );
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void sendAuctionSuccessToSubscriber(Long userId, Long auctionId, String auctionTitle) {
    sendNotification(
        userId,
        auctionId,
        "경매 낙찰 알림",
        "'" + auctionTitle + "' 경매가 낙찰되어 종료되었습니다.",
        NotificationType.SALE_SUCCESS_SUBSCRIBER,
        "auctionSuccessSubscriber"
    );
  }

  @Async("socketTaskExecutor")
  @Transactional
  public void chatNotificationSend(Long receiverId, Long chatRoomId, String senderName, String preview) {

    // 1) 알림 upsert (읽지 않은 동일 채팅방 알림이 있으면 업데이트)
    User user = getUserOrThrow(receiverId);

    Notification saved = notificationRepository.findUnreadChatNotificationOne(receiverId, chatRoomId)
        .map(n -> {
          n.updateReadStatus(false);
          n.updateChatMessage("채팅 알림", senderName + ": " + preview);
          return n;
        })
        .orElseGet(() -> Notification.create(
            user,
            "채팅 알림",
            senderName + ": " + preview,
            false,
            NotificationType.CHAT_MESSAGE,
            chatRoomId
        ));

    Notification persisted = notificationRepository.save(saved);

    // 2) SSE 전송 스로틀 (짧은 시간에 여러 개 오면 토스트 난사 방지)
    if (!shouldPushChatNotification(receiverId, chatRoomId)) {
      return; // DB는 갱신됐으니 실시간 푸시만 스킵
    }

    NotificationReadResponse response = NotificationReadResponse.from(persisted);
    sendToClient(String.valueOf(receiverId), "chatMessage", response);
  }

  private boolean shouldPushChatNotification(Long userId, Long chatRoomId) {
    String key = "chat:noti:cooldown:" + userId + ":" + chatRoomId;
    Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(3));
    return Boolean.TRUE.equals(ok);
  }

  private void sendNotification(
      Long userId,
      Long targetId,
      String title,
      String message,
      NotificationType type,
      String sseEventName
  ) {
    User user = getUserOrThrow(userId);
    Notification notification = Notification.create(
        user,
        title,
        message,
        false,
        type,
        targetId
    );
    saveAndSend(userId, sseEventName, notification);
  }

  private User getUserOrThrow(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
  }

  private void saveAndSend(Long userId, String eventName, Notification notification) {
    Notification savedNotification = notificationRepository.save(notification);
    NotificationReadResponse response = NotificationReadResponse.from(savedNotification);
    String id = String.valueOf(userId);

    sendToClient(id, eventName, response);
  }
}
