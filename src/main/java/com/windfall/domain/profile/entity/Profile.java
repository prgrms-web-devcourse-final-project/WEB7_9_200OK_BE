package com.windfall.domain.profile.entity;

import com.windfall.domain.user.entity.User;
import com.windfall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, name = "profile_image_url")
  private String profileImageUrl;

  @Builder
  public Profile(User user, String email, String name, String profileImageUrl) {
    this.user = user;
    this.email = email;
    this.name = name;
    this.profileImageUrl = profileImageUrl;
  }
}
