package com.windfall.api.tag.service;

import com.windfall.api.auction.dto.request.TagInfo;
import com.windfall.api.tag.dto.request.SearchTagRequest;
import com.windfall.api.tag.dto.response.SearchTagResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final AuctionTagRepository auctionTagRepository;

  @Transactional
  public List<String> saveTagIfExist(Auction auction, List<TagInfo> tags) {
    if (tags == null || tags.isEmpty()) {
      return List.of();
    }
    return saveTag(auction, tags);
  }

  private List<String> saveTag(Auction auction, List<TagInfo> tags) {
    List<String> savedTagNames = new ArrayList<>();

    for (TagInfo tag : tags) {
      Tag savedTag = tagRepository.findByTagName(tag.name())
          .orElseGet(() -> tagRepository.save(Tag.create(tag.name()))
          );

      AuctionTag auctionTag = AuctionTag.create(auction, savedTag);
      auctionTagRepository.save(auctionTag);

      savedTagNames.add(savedTag.getTagName());
    }
    return savedTagNames;
  }

  @Transactional(readOnly = true)
  public SearchTagResponse searchTag(SearchTagRequest request) {
    if (request.keyword() == null || request.keyword().isBlank()) {
      return SearchTagResponse.empty();
    }
    String trimmedKeyword = request.keyword().trim();

    List<String> tags = tagRepository
        .findByKeyword(trimmedKeyword, PageRequest.of(0, 5))
        .stream()
        .map(Tag::getTagName)
        .toList();

    return SearchTagResponse.from(tags);
  }

  @Transactional
  public void deleteTag(Auction auction) { // 하드 딜리트

    List<AuctionTag> auctionTags = auctionTagRepository.findByAuction(auction);

    if (auctionTags.isEmpty()) {
      return;
    }

    List<Tag> tags = auctionTags.stream()
        .map(AuctionTag::getTag)
        .distinct()
        .toList();

    auctionTagRepository.deleteAll(auctionTags);

    for (Tag tag : tags) {
      boolean isUsedElsewhere = auctionTagRepository.existsByTag(tag);

      if (!isUsedElsewhere) {
        tagRepository.delete(tag);
      }
    }
  }
}