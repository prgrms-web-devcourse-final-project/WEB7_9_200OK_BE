package com.windfall.api.tag.service;

import com.windfall.api.auction.dto.request.TagInfo;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  private final AuctionTagRepository auctionTagRepository;
  //private final TagIndexService tagIndexService;

  @Transactional
  public void registerAuctionTags(Auction auction, List<TagInfo> tags) {
    if (tags == null || tags.isEmpty()) {
      return;
    }

    saveAuctionTags(auction, tags);

    //tagIndexService.indexTags(tags);
  }

  private void saveAuctionTags(Auction auction, List<TagInfo> tags) {
    for (TagInfo tag : tags) {
      Tag savedTag = tagRepository.findByTagName(tag.tagName())
          .orElseGet(() -> tagRepository.save(Tag.create(tag.tagName()))
          );

      AuctionTag auctionTag = AuctionTag.create(auction, savedTag);
      auctionTagRepository.save(auctionTag);
    }
  }
}