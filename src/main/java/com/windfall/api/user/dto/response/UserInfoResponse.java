package com.windfall.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoResponse(
    @Schema(description = "이 프로필이 본인인지 확인하는 필드")
    boolean isOwner,

    @Schema(description = "유저 id")
    Long userid,

    @Schema(description = "유저 이름")
    String username,

    @Schema(description = "유저 이메일")
    String email,

    @Schema(description = "유저 프로필 이미지 url")
    String profileImage,

    @Schema(description = "이 사용자가 받은 총 리뷰")
    Long totalReviews,

    @Schema(description = "이 사용자의 평균 별점 (소수점)")
    double rating
) {
}
