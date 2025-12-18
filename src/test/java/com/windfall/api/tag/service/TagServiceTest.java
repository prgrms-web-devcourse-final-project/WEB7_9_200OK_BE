package com.windfall.api.tag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.windfall.api.auction.dto.request.TagInfo;
import com.windfall.api.tag.dto.request.SearchTagRequest;
import com.windfall.api.tag.dto.response.SearchTagResponse;
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

  private Auction auction1;
  private Auction auction2;

  @BeforeEach
  void setUp() {
    User seller = userRepository.save(User.builder()
        .email("test@naver.com")
        .provider(ProviderType.NAVER)
        .providerUserId("test1234")
        .build()
    );

    auction1 = auctionRepository.save(
        Auction.builder()
        .title("경매1")
        .description("테스트 설명")
        .category(AuctionCategory.DIGITAL)
        .startPrice(10000L)
        .currentPrice(10000L)
        .stopLoss(9000L)
        .dropAmount(50L)
        .status(AuctionStatus.SCHEDULED)
        .startedAt(LocalDateTime.now().plusDays(2))
        .seller(seller)
        .build()
    );

    auction2 = auctionRepository.save(
        Auction.builder()
            .title("경매2")
            .description("테스트 설명")
            .category(AuctionCategory.DIGITAL)
            .startPrice(100000L)
            .currentPrice(100000L)
            .stopLoss(90000L)
            .dropAmount(1000L)
            .status(AuctionStatus.SCHEDULED)
            .startedAt(LocalDateTime.now().plusDays(1))
            .seller(seller)
            .build()
    );
  }

  @Test
  @DisplayName("[태그 등록1] 태그 등록 후 Tag와 AuctionTag에 값이 저장되는 경우")
  void createTag1() {
    // given
    List<TagInfo> tags = List.of(
        TagInfo.from("고기"),
        TagInfo.from("고등어"),
        TagInfo.from("군고구마"),
        TagInfo.from("고등어")
    );

    // when
    tagService.saveTagIfExist(auction1, tags);

    // then
    List<Tag> savedTags = tagRepository.findAll();
    assertEquals(3, savedTags.size());

    List<AuctionTag> auctionTags = auctionTagRepository.findAll();
    assertEquals(4, auctionTags.size());
  }

  @Test
  @DisplayName("[태그 등록2] 사용자가 등록한 태그가 없는 경우")
  public void createTag2() {
    //given
    List<TagInfo> tag1 = null;
    List<TagInfo> tag2 = List.of();

    //when
    tagService.saveTagIfExist(auction1, tag1);
    tagService.saveTagIfExist(auction2, tag2);

    //then
    List<Tag> savedTags = tagRepository.findAll();
    assertEquals(0, savedTags.size());

    List<AuctionTag> auctionTags = auctionTagRepository.findAll();
    assertEquals(0, auctionTags.size());
  }

  @Test
  @DisplayName("[태그 검색1] DB에 저장된 태그가 5개 이상일 때, 태그 자동완성을 응답하는 경우")
  void searchTag1() {
    // given: DB에 태그 데이터 저장
    tagRepository.save(Tag.create("나무"));
    tagRepository.save(Tag.create("나비"));
    tagRepository.save(Tag.create("나이키"));
    tagRepository.save(Tag.create("나이키에어"));
    tagRepository.save(Tag.create("나이키운동화"));
    tagRepository.save(Tag.create("나침반")); // 6번째

    SearchTagRequest request = new SearchTagRequest("나");

    // when
    SearchTagResponse response = tagService.searchTag(request);

    // then: 최대 5개만 반환 확인
    assertEquals(5, response.tags().size());
    assertEquals(List.of("나무", "나비", "나이키", "나이키에어", "나이키운동화"),
        response.tags());
  }
  @Test
  @DisplayName("[태그 검색2] 태그 검색어로 앞, 뒤 공백을 넣는 경우")
  void searchTag2() {
    // given: DB에 태그 데이터 저장
    tagRepository.save(Tag.create("나무"));
    tagRepository.save(Tag.create("나비"));
    tagRepository.save(Tag.create("나이키"));
    tagRepository.save(Tag.create("나이키에어"));

    SearchTagRequest request = new SearchTagRequest(" 나 ");

    // when
    SearchTagResponse response = tagService.searchTag(request);

    // then
    assertEquals(4, response.tags().size());
    assertEquals(List.of("나무", "나비", "나이키", "나이키에어"), response.tags());
  }

  @Test
  @DisplayName("[태그 검색3] 태그 검색어로 사이 공백을 넣는 경우")
  void searchTag3() {
    // given: DB에 태그 데이터 저장
    tagRepository.save(Tag.create("나이키"));
    tagRepository.save(Tag.create("나이키에어"));

    SearchTagRequest request = new SearchTagRequest("나 이키");

    // when
    SearchTagResponse response = tagService.searchTag(request);

    // then
    assertEquals(0, response.tags().size());
    assertEquals(List.of(), response.tags());
  }

  @Test
  @DisplayName("[태그 삭제1] 경매 게시물에 태그가 없는데 삭제하는 경우")
  void deleteTag1() {
    //given
    Tag tag1 = tagRepository.save(Tag.create("나무"));
    Tag tag2 = tagRepository.save(Tag.create("나비"));

    auctionTagRepository.save(AuctionTag.create(auction1, tag1));
    auctionTagRepository.save(AuctionTag.create(auction1, tag2));

    // when
    tagService.deleteTag(auction2);

    // then
    assertEquals(2, auctionTagRepository.count());
    assertEquals(2, tagRepository.count());
  }

  @Test
  @DisplayName("[태그 삭제2] 경매 게시물에 태그가 있는데 삭제하는 경우")
  void deleteTag2() {
    //given
    Tag tag1 = tagRepository.save(Tag.create("나무"));
    Tag tag2 = tagRepository.save(Tag.create("나비"));

    auctionTagRepository.save(AuctionTag.create(auction1, tag1));
    auctionTagRepository.save(AuctionTag.create(auction1, tag2));

    // when
    tagService.deleteTag(auction1);

    // then
    assertEquals(0, auctionTagRepository.count());
    assertEquals(0, tagRepository.count());
  }

  @Test
  @DisplayName("[태그 삭제3] 공유 중인 태그를 삭제하는 경우")
  void deleteTag3() {
    // given
    Tag sharedTag = tagRepository.save(Tag.create("공유태그"));

    auctionTagRepository.save(AuctionTag.create(auction1, sharedTag));
    auctionTagRepository.save(AuctionTag.create(auction2, sharedTag));

    // when
    tagService.deleteTag(auction1);

    // then
    assertEquals(1, auctionTagRepository.count());
    assertEquals(1, tagRepository.count());
  }
}