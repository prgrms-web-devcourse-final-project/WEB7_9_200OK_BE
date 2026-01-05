package com.windfall.domain.chat.repository;

import com.windfall.domain.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Query("""
      select cm
      from ChatMessage cm
      join fetch cm.sender s
      where cm.chatRoom.id = :chatRoomId
      order by cm.id desc
      """)
  List<ChatMessage> findLatest(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

  @Query("""
      select cm
      from ChatMessage cm
      join fetch cm.sender s
      where cm.chatRoom.id = :chatRoomId
        and cm.id < :cursor
      order by cm.id desc
      """)
  List<ChatMessage> findOlderThan(
      @Param("chatRoomId") Long chatRoomId,
      @Param("cursor") Long cursor,
      Pageable pageable
  );

  ChatMessage findTopByChatRoomIdOrderByCreateDateDesc(Long chatRoomId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update ChatMessage m
         set m.isRead = true
       where m.chatRoom.id = :chatRoomId
         and m.isRead = false
         and m.sender.id <> :userId
      """)
  int markAllAsReadExcludingSender(
      @Param("chatRoomId") Long chatRoomId,
      @Param("userId") Long userId
  );

  @Query("""
      select max(cm.id)
      from ChatMessage cm
      where cm.chatRoom.id = :chatRoomId
      """)
  Long findMaxMessageId(@Param("chatRoomId") Long chatRoomId);

}
