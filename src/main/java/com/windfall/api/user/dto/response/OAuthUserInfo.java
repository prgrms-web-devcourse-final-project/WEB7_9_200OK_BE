//

package com.windfall.api.user.dto.response;

// 공통 사용자 정보 DTO
public record OAuthUserInfo(
    String providerUserId,
    String email,
    String nickname,
    String profileImageUrl
) {}
