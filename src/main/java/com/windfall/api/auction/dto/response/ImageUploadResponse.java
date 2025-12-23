package com.windfall.api.auction.dto.response;

import com.windfall.domain.auction.entity.AuctionImage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 등록 응답 DTO")
public record ImageUploadResponse(

    @Schema(description = "이미지 Id")
    Long imageId,

    @Schema(description = "이미지 URL")
    String imageUrl
) {

  public static ImageUploadResponse from(AuctionImage auctionImage) {
    return new ImageUploadResponse(auctionImage.getId(), auctionImage.getImage());
  }
}
