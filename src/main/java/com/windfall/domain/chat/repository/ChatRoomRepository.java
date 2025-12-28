package com.windfall.domain.chat.repository;

import com.windfall.domain.chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  @Query("""
      select cr
      from ChatRoom cr
      join fetch cr.trade t
      join fetch t.auction a
      join fetch a.seller s
      where (
        (:scope = 'ALL' and (t.buyerId = :userId or t.sellerId = :userId))
        or (:scope = 'BUY' and t.buyerId = :userId)
        or (:scope = 'SELL' and t.sellerId = :userId)
      )
      order by coalesce(cr.lastMessageAt, cr.createDate) desc
      """)
  List<ChatRoom> findChatRoomForList(
      @Param("userId") Long userId,
      @Param("scope") String scope
  );

  @Query("""
      select cr
      from ChatRoom cr
      join fetch cr.trade t
      join fetch t.auction a
      join fetch a.seller s
      where cr.id = :chatRoomId
      """)
  Optional<ChatRoom> findDetailById(@Param("chatRoomId") Long chatRoomId);

}
