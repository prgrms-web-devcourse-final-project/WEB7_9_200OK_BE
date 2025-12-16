package com.windfall.api.tag.service;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final AuctionTagRepository auctionTagRepository;
  private final TagIndexService tagIndexService;

  private static final int MAX_TAG_COUNT = 5;
  private static final int MAX_TAG_LENGTH = 10;

  @Transactional
  public void registerAuctionTags(Auction auction, List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return;
    }

    validateTags(tags);
    saveAuctionTags(auction, tags);

    tagIndexService.indexTags(tags);
  }

  private void saveAuctionTags(Auction auction, List<String> tagNames) {
    for (String tagName : tagNames) {
      Tag tag = tagRepository.findByTagName(tagName)
          .orElseGet(() -> tagRepository.save(Tag.create(tagName))
          );

      AuctionTag auctionTag = AuctionTag.create(auction, tag);
      auctionTagRepository.save(auctionTag);
    }
  }

  private void validateTags(List<String> tagNames) {
    if (tagNames.size() > MAX_TAG_COUNT) {
      throw new ErrorException(
          String.format("태그는 최대 %d개까지 등록할 수 있습니다.", MAX_TAG_COUNT),
          ErrorCode.TAG_COUNT_EXCEEDED
      );
    }

    for (String tagName : tagNames) {
      if (tagName.isBlank()) {
        throw new ErrorException(ErrorCode.TAG_EMPTY);
      }

      if (tagName.contains(" ")) {
        throw new ErrorException(ErrorCode.TAG_CONTAINS_SPACE);
      }

      if (!tagName.matches("^[가-힣a-zA-Z0-9]+$")) {
        throw new ErrorException(ErrorCode.TAG_INVALID_CHAR);
      }

      if (tagName.length() > MAX_TAG_LENGTH) {
        throw new ErrorException(
            String.format("태그는 최대 %d글자까지 가능합니다.", MAX_TAG_LENGTH),
            ErrorCode.TAG_TOO_LONG
        );
      }
    }
  }
}