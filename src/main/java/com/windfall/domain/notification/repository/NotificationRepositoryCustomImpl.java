package com.windfall.domain.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import static com.windfall.domain.notification.entity.QNotification.notification;

@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public long markAllAsRead(Long userId) {
    return queryFactory
        .update(notification)
        .set(notification.readStatus,true)
        .where(notification.user.id.eq(userId)
            .and(notification.readStatus.isFalse()))
        .execute();
  }
}
