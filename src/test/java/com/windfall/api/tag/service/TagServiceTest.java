package com.windfall.api.tag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.windfall.api.tag.document.TagDocument;
import com.windfall.api.tag.factory.AuctionFactory;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import com.windfall.domain.tag.repository.TagSearchRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@Testcontainers
@Transactional
class TagServiceTest {

  @Autowired
  private TagService tagService;

  @Autowired
  private AuctionRepository auctionRepository;

  @Autowired
  private TagSearchRepository tagSearchRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private AuctionTagRepository auctionTagRepository;

  private Auction auction;

  @Container
  static ElasticsearchContainer esContainer =
      new ElasticsearchContainer(
          "docker.elastic.co/elasticsearch/elasticsearch:8.11.0"
      )
          .withEnv("discovery.type", "single-node")
          .withEnv("xpack.security.enabled", "false");

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.elasticsearch.uris",
        esContainer::getHttpHostAddress
    );
  }

  @BeforeAll
  @DisplayName("ES 인덱스 생성")
  static void setupIndex() throws Exception {
    RestClient client = RestClient.builder(
        HttpHost.create(esContainer.getHttpHostAddress())
    ).build();

    String indexJson = """
      {
        "settings": {
          "analysis": {
            "tokenizer": {
              "edge_ngram_tokenizer": {
                "type": "edge_ngram",
                "min_gram": 1,
                "max_gram": 10,
                "token_chars": ["letter", "digit"]
              }
            },
            "analyzer": {
              "autocomplete_analyzer": {
                "type": "custom",
                "tokenizer": "edge_ngram_tokenizer",
                "filter": ["lowercase"]
              }
            }
          }
        },
        "mappings": {
          "properties": {
            "tag": {
              "type": "text",
              "analyzer": "autocomplete_analyzer",
              "search_analyzer": "standard",
              "fields": {
                "keyword": {
                  "type": "keyword"
                }
              }
            },
            "useCount": {
              "type": "long"
            }
          }
        }
      }
    """;

    Request request = new Request("PUT", "/tags");
    request.setJsonEntity(indexJson);
    client.performRequest(request);
  }

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
  @DisplayName("태그 등록 시 ES 색인이 정상 수행된다")
  void success1() {
    // given
    List<String> tag1 = List.of("고기", "고등어", "군고구마");
    List<String> tag2 = List.of("고기", "나이키", "운동기구");

    // when
    tagService.registerAuctionTags(auction, tag1);
    tagService.registerAuctionTags(auction, tag2);

    // then
    List<TagDocument> indexed = StreamSupport
        .stream(tagSearchRepository.findAll().spliterator(), false)
        .toList();

    assertEquals(5, indexed.size());

    TagDocument first = tagSearchRepository.findById("고기").orElseThrow();
    assertEquals(2L, first.getUseCount());
  }

  @Test
  @DisplayName("태그 등록 후 Tag와 AuctionTag에 값이 저장된다")
  void success2() {
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
}