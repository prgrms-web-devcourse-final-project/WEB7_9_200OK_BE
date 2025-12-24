package com.windfall.api.user.dto.response;

public record OAuthTokenResponse(
    String accessToken,
    String refreshToken
){}
