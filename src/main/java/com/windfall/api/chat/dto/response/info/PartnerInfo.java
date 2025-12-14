package com.windfall.api.chat.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상대방 정보 DTO")
public record PartnerInfo(
    @Schema(description = "상대방 ID")
    Long partnerId,
    @Schema(description = "상대방 닉네임 (현재는 email로 대체")
    String nickname,
    @Schema(description = "상대방 프로필 이미지 URL (현재는 null 또는 아무 값)")
    String profileImageUrl

) {}
