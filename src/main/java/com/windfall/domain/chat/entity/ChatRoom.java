package com.windfall.domain.chat.entity;

import com.windfall.domain.chat.enums.ChatMessageType;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trade_id", nullable = false, unique = true)
  private Trade trade;

  @Column(name = "last_message_at")
  private LocalDateTime lastMessageAt;

  @Column(name = "last_message_preview", length = 200)
  private String lastMessagePreview;

  @Enumerated(EnumType.STRING)
  @Column(name = "last_message_type")
  private ChatMessageType lastMessageType;

  public void updateLastMessage(LocalDateTime at, String preview, ChatMessageType type) {
    this.lastMessageAt = at;
    this.lastMessagePreview = preview;
    this.lastMessageType = type;
  }
}
