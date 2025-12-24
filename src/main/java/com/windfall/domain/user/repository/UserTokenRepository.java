package com.windfall.domain.user.repository;

import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.entity.UserToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
  Optional<UserToken> findByUser(User user);
}
