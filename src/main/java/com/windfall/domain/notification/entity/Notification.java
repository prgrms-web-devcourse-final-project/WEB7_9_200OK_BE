package com.windfall.domain.notification.entity;

import com.windfall.domain.notification.enums.NotificationType;
import com.windfall.domain.user.entity.User;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Notification extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String message;

  @Builder.Default
  private Boolean readStatus = false;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  public static Notification create(User user, String title, String message, Boolean readStatus, NotificationType type){
    return Notification.builder()
        .user(user)
        .title(title)
        .message(message)
        .readStatus(readStatus)
        .type(type)
        .build();
  }
}
