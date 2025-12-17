package com.windfall.api.tag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.windfall.api.auction.dto.request.TagInfo;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TagServiceTest {

  @Autowired
  private TagService tagService;

  @Autowired
  private AuctionRepository auctionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private AuctionTagRepository auctionTagRepository;

  //@Mock
  //private TagSearchRepository tagSearchRepository; // ES Repository를 Mock으로 대체

  private Auction auction;

  @BeforeEach
  void setUp() {
    User seller = userRepository.save(User.builder()
        .email("test@naver.com")
        .provider(ProviderType.NAVER)
        .provideruserId("test1234")
        .build()
    );

    auction = Auction.builder()
        .title("테스트 제목")
        .description("테스트 설명")
        .category(AuctionCategory.DIGITAL)
        .startPrice(10000L)
        .currentPrice(10000L)
        .stopLoss(9000L)
        .dropAmount(50L)
        .status(AuctionStatus.SCHEDULED)
        .startedAt(LocalDateTime.now().plusDays(2))
        .seller(seller)
        .build();

    auction = auctionRepository.save(auction);
  }

  @Test
  @DisplayName("태그 등록 후 Tag와 AuctionTag에 값이 저장되는 경우")
  void success1() {
    // given
    List<TagInfo> tags = List.of(
        TagInfo.from("고기"),
        TagInfo.from("고등어"),
        TagInfo.from("군고구마"),
        TagInfo.from("고등어")
    );

    // when
    tagService.registerAuctionTags(auction, tags);

    // then
    List<Tag> savedTags = tagRepository.findAll();
    assertEquals(3, savedTags.size());

    List<AuctionTag> auctionTags = auctionTagRepository.findAll();
    assertEquals(4, auctionTags.size());
  }

  @Test
  @DisplayName("사용자가 등록한 태그가 없는 경우")
  public void success2() {
    //given
    List<TagInfo> tag1 = null;
    List<TagInfo> tag2 = List.of();

    //when
    tagService.registerAuctionTags(auction, tag1);
    tagService.registerAuctionTags(auction, tag2);

    //then
    List<Tag> savedTags = tagRepository.findAll();
    assertEquals(0, savedTags.size());

    List<AuctionTag> auctionTags = auctionTagRepository.findAll();
    assertEquals(0, auctionTags.size());
  }
}