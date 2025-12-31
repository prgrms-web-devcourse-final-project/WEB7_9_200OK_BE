package com.windfall.api.notification.service;

import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {
  private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;


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
  @Transactional
  public void priceNotificationSend(Long userId,Long targetId,String content) {
    User user = userRepository.findById(userId).orElse(null);
    Notification notification = Notification.create(user,
        "가격 하락 알림",
        "관심 상품의 가격이 하락했습니다: " + content,
        false,
        NotificationType.PRICE_DROP,targetId);
    Notification saveNotification = notificationRepository.save(notification);
    String id = String.valueOf(userId);


    sendToClient(id, "priceAlert", saveNotification);
  }
}
