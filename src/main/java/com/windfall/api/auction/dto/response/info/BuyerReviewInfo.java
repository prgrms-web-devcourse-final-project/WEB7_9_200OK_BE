package com.windfall.api.auction.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;

public record BuyerReviewInfo(
    @Schema(description = "구매자 ID")
    Long buyerId,

    @Schema(description = "구매자 이름")
    String username,

    @Schema(description = "구매자 리뷰 내용 (리뷰를 작성하지 않았을 경우 null)")
    String content
) {
}
