package com.windfall.domain.chat.repository;

import com.windfall.domain.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  interface UnreadCountProjection {
    Long getChatRoomId();
    Long getCnt();
  }

  @Query("""
      select cm.chatRoom.id as chatRoomId,
             count(cm.id) as cnt
      from ChatMessage cm
      where cm.chatRoom.id in :chatRoomIds
        and cm.isRead = false
        and cm.sender.id <> :userId
      group by cm.chatRoom.id
      """)
  List<UnreadCountProjection> countUnreadByChatRoomIds(
      @Param("userId") Long userId,
      @Param("chatRoomIds") List<Long> chatRoomIds
  );

  ChatMessage findTopByChatRoomIdOrderByCreateDateDesc(Long chatRoomId);
}
