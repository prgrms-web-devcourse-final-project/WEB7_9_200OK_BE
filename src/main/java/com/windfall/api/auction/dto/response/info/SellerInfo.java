package com.windfall.api.auction.dto.response.info;

import com.windfall.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자 정보 응답 DTO")
public record SellerInfo(

    @Schema(description = "판매자 ID")
    Long sellerId,

    @Schema(description = "판매자 닉네임")
    String nickname,

    @Schema(description = "판매자 프로필 이미지 URL")
    String profileImageUrl,

    @Schema(description = "판매자 평점")
    double rating,

    @Schema(description = "판매자 리뷰 수")
    Long reviewCount

) {
  public static SellerInfo from(User user) {
    return new SellerInfo(
        user.getId(),
        "닉네임", // user.getNickname(),
        "https://example.png", // user.getProfileImageUrl(),
        0.0, // user.getRating(),
        0L // user.getReviewCount()
    );
  }
}