package com.windfall.api.chat.dto.response.info;

import com.windfall.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상대방 정보 DTO")
public record PartnerInfo(
    @Schema(description = "상대방 ID")
    Long partnerId,
    @Schema(description = "상대방 닉네임")
    String username,
    @Schema(description = "상대방 프로필 이미지 URL")
    String profileImageUrl

) {

  public static PartnerInfo from(User user) {
    return new PartnerInfo(
        user.getId(),
        user.getNickname(),
        user.getProfileImageUrl());
  }
}
