package com.windfall.api.user.dto.response;

public record OAuthTokenResponse(
    Long userId,
    String accessToken,
    String refreshToken
){}
