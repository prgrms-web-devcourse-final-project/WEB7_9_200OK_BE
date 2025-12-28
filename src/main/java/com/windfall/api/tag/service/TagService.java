package com.windfall.api.tag.service;

import com.windfall.api.auction.dto.request.TagInfo;
import com.windfall.api.tag.dto.response.SearchTagInfo;
import com.windfall.api.tag.dto.response.SearchTagResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
  public SearchTagResponse searchTag(String request) {
    if (request == null || request.isBlank()) {
      return SearchTagResponse.empty();
    }

    String trimmedKeyword = request.trim();

    List<AuctionTag> auctionTags = auctionTagRepository.findByKeyword(trimmedKeyword);

    Map<String, SearchTagInfo> tagMap = getStringSearchTagInfoMap(auctionTags);

    List<SearchTagInfo> tags = tagMap.values().stream()
        .limit(5)
        .toList();

    return SearchTagResponse.from(tags);
  }

  private Map<String, SearchTagInfo> getStringSearchTagInfoMap(
      List<AuctionTag> auctionTags) {
    Map<String, SearchTagInfo> tagMap = new LinkedHashMap<>();

    for (AuctionTag at : auctionTags) {
      String tagName = at.getTag().getTagName();
      Long tagId = at.getTag().getId();
      Long auctionId = at.getAuction().getId();

      tagMap.compute(tagName, (key, existing) -> {
        if (existing == null) {
          return new SearchTagInfo(tagName, tagId, new ArrayList<>(List.of(auctionId)));
        } else {
          existing.auctionIds().add(auctionId);
          return existing;
        }
      });
    }
    return tagMap;
  }

  @Transactional
  public void deleteTag(Auction auction) { // 하드 딜리트

    List<AuctionTag> auctionTags = auctionTagRepository.findAllByAuction(auction);

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