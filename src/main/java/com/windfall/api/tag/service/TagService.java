package com.windfall.api.tag.service;

import com.windfall.api.auction.dto.request.TagInfo;
import com.windfall.api.tag.dto.request.SearchTagRequest;
import com.windfall.api.tag.dto.response.SearchTagResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
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
  //private final TagIndexService tagIndexService;

  @Transactional
  public void saveTagsIfExist(Auction auction, List<TagInfo> tags) {
    if (tags == null || tags.isEmpty()) {
      return;
    }
    saveTags(auction, tags);
    //tagIndexService.indexTags(tags);
  }

  private void saveTags(Auction auction, List<TagInfo> tags) {
    for (TagInfo tag : tags) {
      Tag savedTag = tagRepository.findByTagName(tag.name())
          .orElseGet(() -> tagRepository.save(Tag.create(tag.name()))
          );

      AuctionTag auctionTag = AuctionTag.create(auction, savedTag);
      auctionTagRepository.save(auctionTag);
    }
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
}