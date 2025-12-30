package com.windfall.domain.chat.repository;

import com.windfall.domain.chat.entity.ChatImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {

  @Query("""
      select ci
      from ChatImage ci
      join fetch ci.chatMessage cm
      where cm.id in :messageIds
      """)
  List<ChatImage> findByMessageIds(@Param("messageIds") List<Long> messageIds);
}
