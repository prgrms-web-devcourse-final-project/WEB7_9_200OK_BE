package com.windfall.api.auction.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@AutoConfigureMockMvc(addFilters = false)
class AuctionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AuctionRepository auctionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Long sellerId;

  private Long auctionId;


  @BeforeEach
  void setUp() {
    User seller = User.builder()
        .email("test@naver.com")
        .provider(ProviderType.NAVER)
        .providerUserId("test1234")
        .build();
    User saveUser = userRepository.save(seller);
    sellerId = saveUser.getId();

    Auction auction = Auction.builder()
        .title("테스트 제목")
        .description("테스트 설명")
        .category(AuctionCategory.DIGITAL)
        .startPrice(10000L)
        .currentPrice(10000L)
        .stopLoss(9000L)
        .dropAmount(50L)
        .status(AuctionStatus.SCHEDULED)
        .startedAt(LocalDateTime.now().plusDays(2))
        .seller(saveUser)
        .build();
    Auction saveAuction = auctionRepository.save(auction);
    auctionId = saveAuction.getId();
  }
  @Nested
  @DisplayName("경매 생성 API")
  class t1 {

    @Test
    @DisplayName("정상 작동")
    void success() throws Exception{
      // given
      LocalDateTime resultTime = createTime();
      String formattedTime = formated(resultTime);

      AuctionCreateRequest request = new AuctionCreateRequest(
          sellerId,
          "테스트 제목",
          "테스트 설명",
          AuctionCategory.DIGITAL,
          null,
          10000L,
          9000L,
          50L,
          resultTime
      );

      //when
      ResultActions resultActions = mockMvc.perform(
          post("/api/v1/auctions")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("createAuction"))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.status").value("CREATED"))
          .andExpect(jsonPath("$.message").value("경매가 생성되었습니다."))
          .andExpect(jsonPath("$.data.sellerId").value(sellerId))
          .andExpect(jsonPath("$.data.title").value("테스트 제목"))
          .andExpect(jsonPath("$.data.description").value("테스트 설명"))
          .andExpect(jsonPath("$.data.category").value(AuctionCategory.DIGITAL.name()))
          .andExpect(jsonPath("$.data.startPrice").value(10000L))
          .andExpect(jsonPath("$.data.currentPrice").value(10000L))
          .andExpect(jsonPath("$.data.stopLoss").value(9000L))
          .andExpect(jsonPath("$.data.dropAmount").value(50L))
          .andExpect(jsonPath("$.data.status").value(AuctionStatus.SCHEDULED.name()))
          .andExpect(jsonPath("$.data.startAt").value(formattedTime))
          .andDo(print());
    }

    @Test
    @DisplayName("Validation 예외처리 상황 - 제목")
    void fail1() throws Exception{
      // given
      LocalDateTime resultTime = createTime();

      AuctionCreateRequest request = new AuctionCreateRequest(
          sellerId,
          null,
          "테스트 설명",
          AuctionCategory.DIGITAL,
          null,
          10000L,
          9000L,
          50L,
          resultTime
      );

      //when
      ResultActions resultActions = mockMvc.perform(
          post("/api/v1/auctions")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("createAuction"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.message").value("경매 제목은 필수입니다."))
          .andDo(print());
    }

    @Test
    @DisplayName("경매 가격 하락가가 시작가의 0.5% 이하일 때")
    void fail2() throws Exception{
      // given
      LocalDateTime resultTime = createTime();

      AuctionCreateRequest request = new AuctionCreateRequest(
          sellerId,
          "테스트 제목",
          "테스트 설명",
          AuctionCategory.DIGITAL,
          null,
          10000L,
          9000L,
          49L,
          resultTime
      );

      //when
      ResultActions resultActions = mockMvc.perform(
          post("/api/v1/auctions")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("createAuction"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.message").value("경매 하락 가격을 다시 설정해주세요."))
          .andDo(print());
    }

    @Test
    @DisplayName("경매 스탑로스가 시작가의 90% 초과일 때")
    void fail3() throws Exception{
      // given
      LocalDateTime resultTime = createTime();

      AuctionCreateRequest request = new AuctionCreateRequest(
          sellerId,
          "테스트 제목",
          "테스트 설명",
          AuctionCategory.DIGITAL,
          null,
          10000L,
          9001L,
          50L,
          resultTime
      );

      //when
      ResultActions resultActions = mockMvc.perform(
          post("/api/v1/auctions")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("createAuction"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.message").value("경매 Stop Loss을 다시 설정해주세요."))
          .andDo(print());
    }

    @Test
    @DisplayName("경매 시작 시간이 1일 이내일 때")
    void fail4() throws Exception{
      // given
      LocalDateTime resultTime = createTime().minusMinutes(10);

      AuctionCreateRequest request = new AuctionCreateRequest(
          sellerId,
          "테스트 제목",
          "테스트 설명",
          AuctionCategory.DIGITAL,
          null,
          10000L,
          9000L,
          50L,
          resultTime
      );

      //when
      ResultActions resultActions = mockMvc.perform(
          post("/api/v1/auctions")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("createAuction"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
          .andExpect(jsonPath("$.message").value("경매 시간을 다시 설정해주세요."))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("경매 삭제 API")
  class t2 {
    @Test
    @DisplayName("정상 작동")
    void success() throws Exception{
      // when
      ResultActions resultActions = mockMvc.perform(
          delete("/api/v1/auctions/%s".formatted(auctionId))
              .param("userId", String.valueOf(sellerId))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("deleteAuction"))
          .andExpect(status().isNoContent())
          .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 경매일 때")
    void fail1() throws Exception{
      // when
      ResultActions resultActions = mockMvc.perform(
          delete("/api/v1/auctions/%s".formatted(9999L))
              .param("userId", String.valueOf(sellerId))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("deleteAuction"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value("NOT_FOUND"))
          .andExpect(jsonPath("$.message").value("존재하지 않는 경매입니다."))
          .andDo(print());
    }

    @Test
    @DisplayName("경매 판매자가 아닐 때")
    void fail2() throws Exception{
      User seller = User.builder()
          .email("test@naver.com")
          .provider(ProviderType.NAVER)
          .providerUserId("test1234")
          .build();
      User notUser = userRepository.save(seller);
      // when
      ResultActions resultActions = mockMvc.perform(
          delete("/api/v1/auctions/%s".formatted(auctionId))
              .param("userId", String.valueOf(notUser.getId()))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("deleteAuction"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.status").value("FORBIDDEN"))
          .andExpect(jsonPath("$.message").value("해당 경매의 판매자가 아닙니다."))
          .andDo(print());
    }

    @Test
    @DisplayName("진행 중인 경매일 때")
    void fail3() throws Exception{
      // given
      User user = userRepository.findById(sellerId).orElse(null);

      Auction auction = Auction.builder()
          .title("진행 중인 경매")
          .description("테스트 설명")
          .category(AuctionCategory.DIGITAL)
          .startPrice(10000L)
          .currentPrice(9500L)
          .stopLoss(9000L)
          .dropAmount(50L)
          .status(AuctionStatus.PROCESS)
          .startedAt(LocalDateTime.now().minusHours(1))
          .seller(user)
          .build();

      auctionRepository.save(auction);
      auctionId = auction.getId();
      // when
      ResultActions resultActions = mockMvc.perform(
          delete("/api/v1/auctions/%s".formatted(auctionId))
              .param("userId", String.valueOf(sellerId))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("deleteAuction"))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status").value("CONFLICT"))
          .andExpect(jsonPath("$.message").value("현재 상태의 경매는 삭제할 수 없습니다."))
          .andDo(print());

    }
  }

  @Nested
  @DisplayName("경매 취소 API")
  class t3{
    @Test
    @DisplayName("정상 작동")
    void success() throws Exception{
      // when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/auctions/%s".formatted(auctionId))
              .param("userId", String.valueOf(sellerId))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("cancelAuction"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("OK"))
          .andExpect(jsonPath("$.message").value("경매가 취소되었습니다."))
          .andExpect(jsonPath("$.data.auctionId").value(auctionId))
          .andExpect(jsonPath("$.data.status").value(AuctionStatus.CANCELED.name()))
          .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 경매일 때")
    void fail1() throws Exception{
      // when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/auctions/%s".formatted(9999L))
              .param("userId", String.valueOf(sellerId))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("cancelAuction"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value("NOT_FOUND"))
          .andExpect(jsonPath("$.message").value("존재하지 않는 경매입니다."))
          .andDo(print());
    }

    @Test
    @DisplayName("경매 판매자가 아닐 때")
    void fail2() throws Exception{
      User seller = User.builder()
          .email("test@naver.com")
          .provider(ProviderType.NAVER)
          .providerUserId("test1234")
          .build();
      User notUser = userRepository.save(seller);
      // when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/auctions/%s".formatted(auctionId))
              .param("userId", String.valueOf(notUser.getId()))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("cancelAuction"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.status").value("FORBIDDEN"))
          .andExpect(jsonPath("$.message").value("해당 경매의 판매자가 아닙니다."))
          .andDo(print());
    }

    @Test
    @DisplayName("진행 중인 경매일 때")
    void fail3() throws Exception{
      // given
      User user = userRepository.findById(sellerId).orElse(null);

      Auction auction = Auction.builder()
          .title("진행 중인 경매")
          .description("테스트 설명")
          .category(AuctionCategory.DIGITAL)
          .startPrice(10000L)
          .currentPrice(9500L)
          .stopLoss(9000L)
          .dropAmount(50L)
          .status(AuctionStatus.PROCESS)
          .startedAt(LocalDateTime.now().minusHours(1))
          .seller(user)
          .build();

      auctionRepository.save(auction);
      auctionId = auction.getId();
      // when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/auctions/%s".formatted(auctionId))
              .param("userId", String.valueOf(sellerId))  // TODO : 추후 로그인 개발 시 제거 필요
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("cancelAuction"))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status").value("CONFLICT"))
          .andExpect(jsonPath("$.message").value("현재 상태의 경매는 취소할 수 없습니다."))
          .andDo(print());

    }
  }

  @Nested
  @DisplayName("경매 목록 조회 API")
  class t4 {
    @Test
    @DisplayName("정상 작동")
    void success() throws Exception{
      // when
      ResultActions resultActions = mockMvc.perform(
          get("/api/v1/auctions")
              .accept(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(AuctionController.class))
          .andExpect(handler().methodName("readAuctionList"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("OK"))
          .andExpect(jsonPath("$.message").value("경매 목록 조회에 성공했습니다."))
          .andDo(print());
    }
  }

  private LocalDateTime createTime(){
    LocalDateTime now = LocalDateTime.now().plusMinutes(5);

    int minute = now.getMinute();
    int remainder = minute % 5;

    int baseMinute = (remainder == 0 ? minute : minute + (5 - remainder));

    LocalDateTime resultTime = now.withMinute(baseMinute % 60);

    if(baseMinute >= 60){
      resultTime = resultTime.plusHours(1);
    }

    return resultTime.plusDays(1);
  }
  private String formated(LocalDateTime resultTime){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return resultTime.format(formatter);
  }
}