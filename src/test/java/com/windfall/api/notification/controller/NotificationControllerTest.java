package com.windfall.api.notification.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windfall.domain.notification.entity.Notification;
import com.windfall.domain.notification.enums.NotificationType;
import com.windfall.domain.notification.repository.NotificationRepository;
import com.windfall.global.jwt.JwtTest;
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


  @Autowired
  private ObjectMapper objectMapper;




  @BeforeEach
  void setUp() {

    Notification notification1 = Notification.create(
        mockUser,
        "테스트 제목",
        "테스트 메시지",
        false,
        NotificationType.PRICE_DROP,
        1L);

    Notification notification2 = Notification.create(
        mockUser,
        "테스트 제목",
        "테스트 메시지",
        false,
        NotificationType.PRICE_DROP,
        2L);
    notificationRepository.save(notification1);
    notificationRepository.save(notification2);
  }

  @Nested
  @DisplayName("알림 조회 API")
  class t1 {

    @Test
    @DisplayName("정상 작동")
    void success() throws Exception {
      // given


      //when
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
}