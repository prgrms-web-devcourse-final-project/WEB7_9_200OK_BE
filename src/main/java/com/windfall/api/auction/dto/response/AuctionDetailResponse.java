package com.windfall.api.auction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "경매 상세 응답 DTO")
public record AuctionDetailResponse(

    @Schema(description = "경매 ID", example = "1")
    Long auctionId,

    @Schema(description = "상품 제목", example = "도라에몽 피규어")
    String title,

    @Schema(description = "상품 설명", example = "희귀 도라에몽 피규어입니다.")
    String description,

    @Schema(description = "카테고리", example = "피규어")
    String category,

    @Schema(description = "상품 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\"]")
    List<String> imageUrls,

    @Schema(description = "판매자 정보")
    SellerInfo seller,

    @Schema(description = "시작가", example = "15000")
    int startPrice,

    @Schema(description = "현재가", example = "14850")
    int currentPrice,

    @Schema(description = "최소 보장가", example = "5000")
    int stopLoss,

    @Schema(description = "하락 퍼센트", example = "1.0")
    double discountRate,

    @Schema(description = "경매 상태", example = "ONGOING")
    String status,

    @Schema(description = "찜 수", example = "25")
    int likeCount,

    @Schema(description = "사용자 찜 여부", example = "true")
    boolean isLiked,

    @Schema(description = "실시간 접속자 수", example = "150")
    long viewCount,

    @Schema(description = "경매 시작 시간", example = "2024-06-01T12:00:00")
    LocalDateTime startedAt,

    @Schema(description = "최근 가격 하락 내역", example = "[{\"timestamp\":\"2024-06-01T12:05:00\",\"price\":14850}]")
    List<AuctionHistoryResponse> recentPriceHistory

) {}

