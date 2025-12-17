package com.windfall.domain.user.repository;

import com.windfall.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByProviderUserId(String providerUserId);
}
