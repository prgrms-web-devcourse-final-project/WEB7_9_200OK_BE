package com.windfall.domain.auction.enums;

import lombok.Getter;

@Getter
public enum AuctionCategory {
  CLOTHING("의류"),
  GOODS("잡화"),
  FURNITURE_INTERIOR("가구/인테리어"),
  DIGITAL("디지털"),
  APPLIANCE("가전제품"),
  SPORTS_LEISURE("스포츠/레저"),
  PET("반려동물"),
  HOBBY("취미"),
  BOOK_TICKET("도서/티켓"),
  ETC("기타");

  private final String categoryName;

  AuctionCategory(String categoryName) {
    this.categoryName = categoryName;
  }
}
