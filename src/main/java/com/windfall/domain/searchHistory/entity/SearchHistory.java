package com.windfall.domain.searchHistory.entity;

import com.windfall.domain.user.entity.User;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE search_history SET activated = false WHERE id = ?")
@SQLRestriction("activated = true")
public class SearchHistory extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false)
  private String keyword;

  public static SearchHistory create(User user, String keyword) {
    return SearchHistory.builder()
        .user(user)
        .keyword(keyword)
        .build();
  }
}
