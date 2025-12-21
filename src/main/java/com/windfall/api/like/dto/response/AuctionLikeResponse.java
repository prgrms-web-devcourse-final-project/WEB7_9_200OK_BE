package com.windfall.api.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "찜 등록/해제 응답 DTO")
public record AuctionLikeResponse(

    @Schema(description = "사용자 찜 여부")
    boolean isLiked,

    @Schema(description = "찜 개수")
    long likeCount
) {
    public static AuctionLikeResponse of (boolean isLiked, long likeCount) {
        return new AuctionLikeResponse(isLiked, likeCount);
    }
}