//
// 팩토리패턴 사용한 생성자 함수는 DTO 짜고 추가하겠습니다.
// 다른 엔티티와 연관관계 매핑이 필요한 필드는 다음 PR 때 진행하겠습니다.
// 사유: 다른 엔티티도 push하면 PR에서 검토할 파일 수가 증가하기 때문입니다.
package com.windfall.domain.user.entity;

import com.windfall.domain.user.enums.ProviderType;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProviderType provider;

  @Column(nullable = false, name = "provider_user_id")
  private String providerUserId;

  @Column(nullable = false)
  private String email;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Column(nullable = true)
  private String refreshToken;

  // 필드 추가 시 여기도 손봐주세요.
  @Builder
  public User(ProviderType provider, String providerUserId, String email, String nickname,
      String profileImageUrl) {

    this.provider = provider;
    this.providerUserId = providerUserId;
    this.email = email;
    this.nickname = nickname;
    this.profileImageUrl = profileImageUrl;
  }
}
