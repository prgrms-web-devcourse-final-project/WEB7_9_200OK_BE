package com.windfall.domain.user.entity;

import com.windfall.domain.user.enums.ProviderType;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class CustomUserDetails implements UserDetails {
  private final Long userId;
  private final ProviderType provider;
  private final String providerUserId;
  private final String email;
  private final String nickname;
  private final String profileImageUrl;

  public CustomUserDetails(User user) {
    exceptionUser(user);
    this.userId = user.getId();
    this.provider = user.getProvider();
    this.providerUserId = user.getProviderUserId();
    this.email = user.getEmail();
    this.nickname = user.getNickname();
    this.profileImageUrl = user.getProfileImageUrl();
  }

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getNickname() {
    return nickname;
  }

  public ProviderType getProvider() {
    return provider;
  }

  public String getProviderUserId() {
    return providerUserId;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return provider + "_" + providerUserId;
  }

  private void exceptionUser(User user) {
    if (user.getId() == null) {
      throw new ErrorException(ErrorCode.NOT_FOUND_USER);
    }
  }
}
