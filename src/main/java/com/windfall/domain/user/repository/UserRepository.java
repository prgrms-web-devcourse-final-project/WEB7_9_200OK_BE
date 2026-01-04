package com.windfall.domain.user.repository;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByProviderUserId(String providerUserId);

  boolean existsByEmail(String email);
}
