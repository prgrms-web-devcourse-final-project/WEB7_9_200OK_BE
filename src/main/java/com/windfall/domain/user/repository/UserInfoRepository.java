package com.windfall.domain.user.repository;

import com.windfall.api.user.dto.response.UserInfoResponse;
import com.windfall.api.user.dto.response.reviewlist.ReviewListRaw;
import com.windfall.domain.user.entity.User;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInfoRepository extends JpaRepository<User, Long> {
  @Query("""
  SELECT
  new com.windfall.api.user.dto.response.UserInfoResponse(
  CASE
    WHEN :loginId IS NOT NULL AND u.id = :loginId THEN true
    ELSE false
  END,
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

  @Query("""
  SELECT
  new com.windfall.api.user.dto.response.reviewlist.ReviewListRaw(
  r.id, a.id, u.id, u.nickname, u.profileImageUrl, r.createDate, r.rating / 10.0, r.content, a.title)
  FROM Review r
  JOIN r.trade t
  JOIN t.auction a
  JOIN User u ON u.id = t.buyerId
  WHERE a.seller.id = :id AND t.status = "PURCHASE_CONFIRMED"
  ORDER BY r.createDate DESC
""")
  Slice<ReviewListRaw> getUserReviewList(@Param("id") Long userId, Pageable pageable);



}
