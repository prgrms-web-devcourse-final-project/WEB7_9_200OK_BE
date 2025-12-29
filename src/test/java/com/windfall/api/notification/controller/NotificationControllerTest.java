package com.windfall.api.notification.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.windfall.api.auction.controller.AuctionController;
import com.windfall.api.auction.dto.request.AuctionCreateRequest;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.global.jwt.JwtTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
class NotificationControllerTest extends JwtTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationRepository notificationRepository;



  private Long notificationId;


  @BeforeEach
  void setUp() {

    Notification notification1 = Notification.create(
        mockUser,
        "테스트 제목",
        "테스트 메시지",
        false,
        NotificationType.REVIEW_REGISTERED,
        1L);

    Notification notification2 = Notification.create(
        mockUser,
        "테스트 제목",
        "테스트 메시지",
        false,
        NotificationType.PRICE_DROP,
        2L);
    Notification savedNotification = notificationRepository.save(notification1);
    notificationId = savedNotification.getId();
    notificationRepository.save(notification2);
  }

  @Nested
  @DisplayName("알림 조회 API")
  class t1{
    @Test
    @DisplayName("정상 작동")
    void success() throws Exception{
      // given
      // when
      ResultActions resultActions = mockMvc.perform(
          get("/api/v1/notifications/")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .param("page","0")
      );

      // then
      resultActions
          .andExpect(handler().handlerType(NotificationController.class))
          .andExpect(handler().methodName("readNotification"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("OK"))
          .andExpect(jsonPath("$.message").value("알림 조회에 성공했습니다."))
          .andExpect(jsonPath("$.data.timeStamp").exists())
          .andExpect(jsonPath("$.data.page").value(0))
          .andExpect(jsonPath("$.data.size").value(15))
          .andExpect(jsonPath("$.data.hasNext").value(false))
          .andExpect(jsonPath("$.data.slice[0].type").value("PRICE_DROP"))
          .andExpect(jsonPath("$.data.slice[0].title").value("테스트 제목"))
          .andExpect(jsonPath("$.data.slice[0].message").value("테스트 메시지"))
          .andExpect(jsonPath("$.data.slice[0].readStatus").value(false))
          .andExpect(jsonPath("$.data.slice[0].notificationAt").exists())
          .andExpect(jsonPath("$.data.slice[0].targetId").value(2L))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("알림 단건 읽음 처리 API")
  class t2 {

    @Test
    @DisplayName("정상 작동")
    void success() throws Exception {
      // given


      //when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/notifications/%s".formatted(notificationId))
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(NotificationController.class))
          .andExpect(handler().methodName("markAsRead"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("OK"))
          .andExpect(jsonPath("$.message").value("알림이 읽음 처리되었습니다."))
          .andExpect(jsonPath("$.data.notificationId").exists())
          .andExpect(jsonPath("$.data.type").value("REVIEW_REGISTERED"))
          .andExpect(jsonPath("$.data.target").value("review"))
          .andExpect(jsonPath("$.data.targetId").value(1L))
          .andExpect(jsonPath("$.data.readStatus").value(true))
          .andDo(print());
    }

    @Test
    @DisplayName("본인 알림이 아닐 때")
    void fail1() throws Exception{
      // given
      User seller = User.builder()
          .email("test@naver.com")
          .provider(ProviderType.NAVER)
          .providerUserId("test1234")
          .nickname("testNickname")
          .build();
      User saveUser = userRepository.save(seller);
      Notification notification = Notification.create(
          saveUser,
          "테스트 제목",
          "테스트 메시지",
          false,
          NotificationType.PRICE_DROP,
          2L);
      Notification savedNotification = notificationRepository.save(notification);
      notificationId = savedNotification.getId();

      //when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/notifications/%s".formatted(notificationId))
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(NotificationController.class))
          .andExpect(handler().methodName("markAsRead"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.status").value("FORBIDDEN"))
          .andExpect(jsonPath("$.message").value("해당 유저의 알림이 아닙니다."))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("알림 다건 읽음 처리 API")
  class t3 {

    @Test
    @DisplayName("정상 작동")
    void success() throws Exception {
      // given


      //when
      ResultActions resultActions = mockMvc.perform(
          patch("/api/v1/notifications/")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      resultActions
          .andExpect(handler().handlerType(NotificationController.class))
          .andExpect(handler().methodName("markAllAsRead"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("OK"))
          .andExpect(jsonPath("$.message").value("알림이 모두 읽음 처리되었습니다."))
          .andExpect(jsonPath("$.data.userId").value(mockUser.getId()))
          .andExpect(jsonPath("$.data.updatedCount").value(2))
          .andDo(print());
    }
  }
}