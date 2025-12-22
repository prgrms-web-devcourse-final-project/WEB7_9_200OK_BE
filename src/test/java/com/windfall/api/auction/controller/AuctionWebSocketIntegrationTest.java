//package com.windfall.api.auction.controller;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//
//import com.windfall.api.auction.dto.request.SellerEmojiRequest;
//import com.windfall.api.auction.dto.response.message.SellerEmojiMessage;
//import com.windfall.api.auction.service.AuctionViewerService;
//import com.windfall.domain.auction.entity.Auction;
//import com.windfall.domain.auction.enums.EmojiType;
//import com.windfall.domain.auction.repository.AuctionRepository;
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.Transport;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//// 1. 실제 서버를 랜덤 포트로 띄움
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//    properties = {
//        "custom.jwt.secretPattern=test_secret_key_must_be_long_enough_for_hs256_algorithm_12345",
//        "custom.jwt.expiration=3600000" // 만약 만료시간 설정도 환경변수라면 같이 추가
//    })
//class AuctionWebSocketIntegrationTest {
//
//  @LocalServerPort
//  private int port;
//
//  private WebSocketStompClient stompClient;
//
//  @MockitoBean
//  private AuctionViewerService auctionViewerService;
//
//  @MockitoBean
//  private AuctionRepository auctionRepository;
//
//  @BeforeEach
//  void setup() {
//    StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
//    WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
//    List<Transport> transports = List.of(webSocketTransport);
//    SockJsClient sockJsClient = new SockJsClient(transports);
//
//    stompClient = new WebSocketStompClient(sockJsClient);
//    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//  }
//
//  @Test
//  @DisplayName("통합 테스트: 판매자가 이모지를 보내면 구독자에게 브로드캐스팅된다.")
//  void verifyEmojiBroadcast() throws Exception {
//    // given
//    Long auctionId = 1L;
//    Long sellerId = 1L;
//
//    Auction mockAuction = Mockito.mock(Auction.class);
//
//    given(mockAuction.isSeller(sellerId)).willReturn(true);
//
//    given(auctionRepository.findById(auctionId)).willReturn(Optional.of(mockAuction));
//
//    given(auctionViewerService.addViewer(anyLong(), anyString())).willReturn(1L);
//    given(auctionViewerService.removeViewer(anyString())).willReturn(1L);
//
//    String url = "ws://localhost:" + port + "/ws-stomp";
//    BlockingQueue<SellerEmojiMessage> blockingQueue = new LinkedBlockingQueue<>();
//
//    given(auctionViewerService.addViewer(anyLong(), anyString())).willReturn(1L);
//    given(auctionViewerService.removeViewer(anyString())).willReturn(1L);
//
//    // 3. 서버에 연결 (Connect)
//    StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
//    }).get(1, SECONDS);
//
//    // 4. 구독 (Subscribe) - "/topic/auction/1"
//    // 메시지가 오면 blockingQueue에 넣도록 핸들러 설정
//    session.subscribe("/topic/auction/1", new StompFrameHandler() {
//      @Override
//      public Type getPayloadType(StompHeaders headers) {
//        return SellerEmojiMessage.class; // 받을 DTO 타입
//      }
//
//      @Override
//      public void handleFrame(StompHeaders headers, Object payload) {
//        blockingQueue.offer((SellerEmojiMessage) payload);
//      }
//    });
//
//    // 5. 메시지 전송 (Send) - 판매자가 이모지 보냄
//    // Controller의 @MessageMapping("/auctions/{auctionId}/emoji") 경로
//    SellerEmojiRequest request = new SellerEmojiRequest(EmojiType.FIRE);
//
//    StompHeaders headers = new StompHeaders();
//    headers.setDestination("/app/auctions/1/emoji");
//    headers.add("userId", "1"); // 헤더에 판매자 ID (1L) 포함 (Controller 로직 통과용)
//
//    session.send(headers, request);
//
//    // Then
//    // 6. 결과 검증 - 브로드캐스팅된 메시지가 큐에 들어왔는지 확인 (3초 대기)
//    SellerEmojiMessage response = blockingQueue.poll(3, SECONDS);
//
//    assertThat(response).isNotNull();
//    assertThat(response.emojiType()).isEqualTo(EmojiType.FIRE);
//    assertThat(response.auctionId()).isEqualTo(1L);
//  }
//}
//
