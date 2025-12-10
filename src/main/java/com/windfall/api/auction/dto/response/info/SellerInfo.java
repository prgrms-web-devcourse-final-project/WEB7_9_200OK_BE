package com.windfall.api.auction.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "판매자 정보 응답 DTO")
public record SellerInfo(

    @Schema(description = "판매자 ID", example = "1")
    Long sellerId,

    @Schema(description = "판매자 닉네임", example = "김판매")
    String nickname,

    @Schema(description = "판매자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    String profileImageUrl,

    @Schema(description = "판매자 평점", example = "4.5")
    double rating,

    @Schema(description = "판매자 리뷰 수", example = "25")
    int reviewCount

) {}