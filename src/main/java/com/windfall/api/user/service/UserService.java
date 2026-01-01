package com.windfall.api.user.service;

import com.windfall.api.user.dto.response.OAuthTokenResponse;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.entity.UserToken;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.domain.user.repository.UserTokenRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserTokenRepository userTokenRepository;
  private final JwtProvider jwtProvider;

  @Transactional(readOnly = true)
  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
  }

  @Transactional(readOnly = true)
  public User getUserByProviderUserId(String providerUserId) {
    return userRepository.findByProviderUserId(providerUserId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
  }

  @Transactional(readOnly = true)
  public Map<Long, User> getUsersMapByIds(Set<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }

    return userRepository.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, u -> u));
  }

  @Transactional
  public OAuthTokenResponse regenerateAccessToken(HttpServletRequest request) {

    String refreshToken = jwtProvider.extractRefreshTokenFromCookie(request);
    Long userId = jwtProvider.extractUserId(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
    UserToken userToken = userTokenRepository.findByUser(user)
        .orElseThrow(() -> new ErrorException(ErrorCode.INVALID_REFRESH_TOKEN));
    String userRefreshToken = userToken.getRefreshToken();

    // Null Point Exception 방어를 위해 .equals 대신 Objects.equals() 사용
    if(!Objects.equals(refreshToken, userRefreshToken)){
      throw new ErrorException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    String newRefreshToken = jwtProvider.generateRefreshToken(userId, user.getProviderUserId());
    userToken.saveRefreshToken(newRefreshToken);

    String newAccessToken = jwtProvider.generateAccessToken(userId, user.getProviderUserId());

    return new OAuthTokenResponse(userId, newAccessToken, newRefreshToken);
  }
}
