package com.windfall.api.user.service;

import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

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
    if (userIds == null || userIds.isEmpty()) return Map.of();

    return userRepository.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, u -> u));
  }
}
