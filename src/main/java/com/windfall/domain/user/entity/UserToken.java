package com.windfall.domain.user.entity;

import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserToken  extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String refreshToken;

  protected UserToken(User user, String refreshToken) {
    this.user = user;
    this.refreshToken = refreshToken;
  }

  public static UserToken create(User user, String refreshToken) {
    return new UserToken(user, refreshToken);
  }
  public void saveRefreshToken(String newRefreshToken) {
    this.refreshToken = newRefreshToken;
  }
}
