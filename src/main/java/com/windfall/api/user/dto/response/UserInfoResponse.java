package com.windfall.api.user.dto.response;

public record UserInfoResponse(
  boolean isOwner,
  Long userid,
  String username,
  String email,
  String profileImage,
  Long totalReviews,
  double rating) {
}
