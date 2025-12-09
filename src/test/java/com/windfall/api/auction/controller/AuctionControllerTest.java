package com.windfall.api.auction.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

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


  @BeforeEach
  void setUp() {
    User seller = User.builder()
        .email("test@naver.com")
        .provider(ProviderType.NAVER)
        .provideruserId("test1234")
        .build();
    userRepository.save(seller);
    sellerId = seller.getId();
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
    private LocalDateTime createTime(){
      LocalDateTime now = LocalDateTime.now().plusMinutes(5);

      int minute = now.getMinute();
      int remainder = minute % 5;

      int baseMinute = (remainder == 0 ? minute : minute + (5 - remainder));

      LocalDateTime resultTime = now.withMinute(baseMinute % 60);

      if(baseMinute >= 60){
        resultTime = resultTime.plusHours(1);
      }

      return resultTime;
    }
    private String formated(LocalDateTime resultTime){
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      return resultTime.format(formatter);
    }
  }

}