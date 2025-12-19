package com.windfall.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.review.entity.Review;
import com.windfall.domain.review.repository.ReviewRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.trade.repository.TradeRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) //filterchain 비활성화
class UserinfoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuctionRepository auctionRepository;

  @Autowired
  private TradeRepository tradeRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  private Long userId1;
  private Long userId2;

  @BeforeEach
  //select문 안 꼬이는지 테스트를 위해 여러 데이터를 넣었습니다.
  void init(){
    User user1 = User.builder()
        .email("user1@naver.com")
        .profileImageUrl("testurl.com")
        .provider(ProviderType.NAVER)
        .providerUserId("test")
        .nickname("naveruser1")
        .build();

    User user2 = User.builder()
        .email("user2@naver.com")
        .profileImageUrl("testurl2.com")
        .provider(ProviderType.NAVER)
        .providerUserId("test")
        .nickname("naveruser2")
        .build();

    User u1 = userRepository.save(user1);
    User u2 = userRepository.save(user2);

    userId1 = u1.getId();
    userId2 = u2.getId();

    Auction auction1 = Auction.builder()
        .title("종료된 경매")
        .description("설명")
        .category(AuctionCategory.DIGITAL)
        .startPrice(1000L)
        .currentPrice(500L)
        .stopLoss(100L)
        .dropAmount(50L)
        .status(AuctionStatus.COMPLETED)
        .startedAt(LocalDateTime.now().plusDays(2))
        .seller(u1)
        .build();


    Auction auction2 = Auction.builder()
        .title("종료된 경매2")
        .description("설명2")
        .category(AuctionCategory.DIGITAL)
        .startPrice(1000L)
        .currentPrice(500L)
        .stopLoss(100L)
        .dropAmount(50L)
        .status(AuctionStatus.COMPLETED)
        .startedAt(LocalDateTime.now().plusDays(2))
        .seller(u2)
        .build();

    auctionRepository.save(auction1);
    auctionRepository.save(auction2);

    Trade trade1 = Trade.builder()
        .auction(auction1)
        .sellerId(userId1)
        .buyerId(userId2)
        .status(TradeStatus.PURCHASE_CONFIRMED)
        .finalPrice(500L)
        .build();

    Trade trade2 = Trade.builder()
        .auction(auction2)
        .sellerId(userId2)
        .buyerId(userId1)
        .status(TradeStatus.PURCHASE_CONFIRMED)
        .finalPrice(500L)
        .build();

    tradeRepository.save(trade1);
    tradeRepository.save(trade2);

    Review review1 = Review.builder()
        .trade(trade1)
        .content("좋았습니다")
        .rating(43) // 4.3
        .build();

    Review review2 = Review.builder()
        .trade(trade2)
        .content("굿굿")
        .rating(50) // 5.0
        .build();

    reviewRepository.save(review1);
    reviewRepository.save(review2);
  }

  @Test
  @DisplayName("로그인한 사용자가 자신의 프로필을 봤을 경우")
  void getUserInfoSelfTest() throws Exception{
    //given
    //when
    ResultActions resultActions = mockMvc.perform( //임의로 요청 보내기
        get("/api/v1/users/{userid}?loginId={loginId}", userId1, userId1)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("사용자 정보 조회에 성공했습니다."))
        .andExpect(jsonPath("$.data.isOwner").value(true))
        .andExpect(jsonPath("$.data.userid").value(userId1))
        .andExpect(jsonPath("$.data.username").value("naveruser1"))
        .andExpect(jsonPath("$.data.email").value("user1@naver.com"))
        .andExpect(jsonPath("$.data.profileImage").value("testurl.com"))
        .andExpect(jsonPath("$.data.totalReviews").value(1L))
        .andExpect(jsonPath("$.data.rating").value(4.3));
  }

  @Test
  @DisplayName("로그인한 사용자가 다른사람의 프로필을 봤을 경우")
  void getUserInfoOtherTest() throws Exception{
    //given
    //when
    ResultActions resultActions = mockMvc.perform( //임의로 요청 보내기
        get("/api/v1/users/{userid}?loginId={loginId}", userId1, userId2)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.message").value("사용자 정보 조회에 성공했습니다."))
        .andExpect(jsonPath("$.data.isOwner").value(false))
        .andExpect(jsonPath("$.data.userid").value(userId1))
        .andExpect(jsonPath("$.data.username").value("naveruser1"))
        .andExpect(jsonPath("$.data.email").value("user1@naver.com"))
        .andExpect(jsonPath("$.data.profileImage").value("testurl.com"))
        .andExpect(jsonPath("$.data.totalReviews").value(1L))
        .andExpect(jsonPath("$.data.rating").value(4.3));
  }

  @Test
  @DisplayName("사용자 조회에 실패한 경우 (존재하지 않는 사용자)")
  void getUserInfoFailed() throws Exception{
    //given
    //when
    ResultActions resultActions = mockMvc.perform( //임의로 요청 보내기
        get("/api/v1/users/500?loginId=1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."))
        .andExpect(jsonPath("$.data").isEmpty());
  }

}