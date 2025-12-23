package com.windfall.domain.user.repository;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByProviderUserId(String providerUserId);

  @Query("""
  SELECT
  new com.windfall.api.user.dto.response.UserInfoResponse(
  (u.id = :loginId),
  u.id,
  u.nickname,
  u.email,
  u.profileImageUrl,
  COUNT(r.id),
  COALESCE(CAST(AVG(r.rating / 10.0) as double), 0.0)
  )
  FROM User u
  LEFT JOIN Trade t ON u.id = t.sellerId AND t.status = "PURCHASE_CONFIRMED"
  LEFT JOIN Review r ON r.trade.id = t.id
  WHERE u.id = :id
  GROUP BY u.id, u.nickname, u.email, u.profileImageUrl
""")
  UserInfoResponse findByUserInfo(@Param("id") Long id, @Param("loginId") Long loginId);

  boolean existsByEmail(String email);
}
