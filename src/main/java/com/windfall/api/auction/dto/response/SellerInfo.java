package com.windfall.api.auction.dto.response;

public record SellerInfo(

    Long sellerId,
    String nickname,
    String profileImageUrl,
    double rating,
    int reviewCount

) {}