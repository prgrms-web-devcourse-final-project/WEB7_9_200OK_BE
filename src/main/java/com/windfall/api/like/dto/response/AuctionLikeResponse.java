package com.windfall.api.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "찜 등록/해제 응답 DTO")
public record AuctionLikeResponse(

    @Schema(description = "찜 활성화 여부")
    boolean like,

    @Schema(description = "찜 개수")
    long likeCount
) {
    public static AuctionLikeResponse of (boolean like, long likeCount) {
        return new AuctionLikeResponse(like, likeCount);
    }
}