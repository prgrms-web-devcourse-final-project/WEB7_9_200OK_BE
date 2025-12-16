package com.windfall.api.tag.service;

import static com.windfall.global.exception.ErrorCode.TAG_TOO_LONG;
import static com.windfall.global.exception.ErrorCode.TAG_CONTAINS_SPACE;
import static com.windfall.global.exception.ErrorCode.TAG_COUNT_EXCEEDED;
import static com.windfall.global.exception.ErrorCode.TAG_EMPTY;
import static com.windfall.global.exception.ErrorCode.TAG_INVALID_CHAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.windfall.WindfallApplication;
import com.windfall.api.tag.factory.AuctionFactory;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.global.exception.ErrorException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest(classes = WindfallApplication.class)
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

  private Auction auction;

  @BeforeEach
  void setUp() {
    User seller = userRepository.save(User.builder()
        .email("test@naver.com")
        .provider(ProviderType.NAVER)
        .provideruserId("test1234")
        .build()
    );

    auction = auctionRepository.save(AuctionFactory.createTestAuction(seller));
  }

  @Test
  @DisplayName("태그 등록 후 Tag와 AuctionTag에 값이 저장되는 경우")
  void success1() {
    // given
    List<String> tags = List.of("고기", "고등어", "군고구마", "고등어");

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
    List<String> tag1 = null;
    List<String> tag2 = List.of();

    //when
    tagService.registerAuctionTags(auction, tag1);
    tagService.registerAuctionTags(auction, tag2);

    //then
    List<Tag> savedTags = tagRepository.findAll();
    assertEquals(0, savedTags.size());

    List<AuctionTag> auctionTags = auctionTagRepository.findAll();
    assertEquals(0, auctionTags.size());
  }

  @Test
  @DisplayName("태그에 공백이 있는 경우")
  public void exception1() {
    //given
    List<String> tags = List.of("가방", "고구마", "식 탁");

    // when & then
    ErrorException exception = assertThrows(
        ErrorException.class,
        () -> tagService.registerAuctionTags(auction, tags)
    );

    assertEquals(TAG_CONTAINS_SPACE, exception.getErrorCode());
  }

  @Test
  @DisplayName("태그 최대 개수를 초과한 경우")
  public void exception2() {
    //given
    List<String> tags = List.of("가방", "고구마", "식탁", "인형", "자전거", "노트북");

    // when & then
    ErrorException exception = assertThrows(
        ErrorException.class,
        () -> tagService.registerAuctionTags(auction, tags)
    );

    assertEquals(TAG_COUNT_EXCEEDED, exception.getErrorCode());
  }

  @Test
  @DisplayName("단일 태그의 값이 없는 경우")
  public void exception3() {
    //given
    List<String> tags = List.of("가방", "고구마", "", "식탁");

    // when & then
    ErrorException exception = assertThrows(
        ErrorException.class,
        () -> tagService.registerAuctionTags(auction, tags)
    );

    assertEquals(TAG_EMPTY, exception.getErrorCode());
  }

  @Test
  @DisplayName("태그에 허용되지 않은 문자가 있는 경우1")
  public void exception4() {
    //given
    List<String> tags = List.of("가%방", "고구마", "식탁");

    // when & then
    ErrorException exception = assertThrows(
        ErrorException.class,
        () -> tagService.registerAuctionTags(auction, tags)
    );

    assertEquals(TAG_INVALID_CHAR, exception.getErrorCode());
  }
  @Test
  @DisplayName("태그에 허용되지 않은 문자가 있는 경우2")
  public void exception5() {
    //given
    List<String> tags = List.of("가방", "고구마🍠", "식탁");

    // when & then
    ErrorException exception = assertThrows(
        ErrorException.class,
        () -> tagService.registerAuctionTags(auction, tags)
    );

    assertEquals(TAG_INVALID_CHAR, exception.getErrorCode());
  }

  @Test
  @DisplayName("태그가 최대 글자 수를 초과한 경우")
  public void exception6() {
    //given
    List<String> tags = List.of("가방", "고구마진짜맛있어요꼭사세요", "식탁");

    // when & then
    ErrorException exception = assertThrows(
        ErrorException.class,
        () -> tagService.registerAuctionTags(auction, tags)
    );

    assertEquals(TAG_TOO_LONG, exception.getErrorCode());
  }
}