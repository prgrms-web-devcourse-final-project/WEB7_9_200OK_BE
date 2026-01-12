package com.windfall.domain.notification.repository;

import com.windfall.domain.notification.entity.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification,Long>, NotificationRepositoryCustom {
  Slice<Notification> findByUserId(Long userId, Pageable pageable);

  // 읽지 않은 채팅 알림(채팅방 단위 1개) 조회
  @Query("""
      select n
        from Notification n
       where n.user.id = :userId
         and n.type = com.windfall.domain.notification.enums.NotificationType.CHAT_MESSAGE
         and n.targetId = :chatRoomId
         and n.readStatus = false
       order by n.createDate desc
      """)
  List<Notification> findUnreadChatNotification(
      @Param("userId") Long userId,
      @Param("chatRoomId") Long chatRoomId,
      Pageable pageable
  );

  default Optional<Notification> findUnreadChatNotificationOne(Long userId, Long chatRoomId) {
    List<Notification> list = findUnreadChatNotification(userId, chatRoomId, PageRequest.of(0, 1));
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }

  // 채팅방 들어오거나 읽음 처리 시 채팅 알림 읽음 동기화
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Notification n
         set n.readStatus = true
       where n.user.id = :userId
         and n.type = com.windfall.domain.notification.enums.NotificationType.CHAT_MESSAGE
         and n.targetId = :chatRoomId
         and n.readStatus = false
      """)
  int markChatRoomAsRead(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);
}