package com.windfall.global.websocket;

import com.windfall.api.auction.dto.response.message.AuctionViewerMessage;
import com.windfall.api.auction.service.AuctionViewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final AuctionViewerService viewerService;

  @EventListener
  public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String destination = headerAccessor.getDestination();
    String sessionId = headerAccessor.getSessionId();

    if (destination != null && destination.startsWith("/topic/auction/")) {
      try {
        Long auctionId = Long.parseLong(destination.replace("/topic/auction/", ""));

        long currentCount = viewerService.addViewer(auctionId, sessionId);

        broadcastViewerCount(auctionId, currentCount);

      } catch (NumberFormatException e) {
        log.warn("Invalid auction ID in destination: {}", destination);
      }
    }
  }

  @EventListener
  public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    Long auctionId = viewerService.removeViewer(sessionId);

    if (auctionId != null) {
      long currentCount = viewerService.getViewerCount(auctionId);
      broadcastViewerCount(auctionId, currentCount);
    }
  }

  public void broadcastViewerCount(Long auctionId, long count) {
    AuctionViewerMessage message = AuctionViewerMessage.of(auctionId, count);
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);
    log.info("Broadcast auctionId : {}, viewer count: {}", auctionId, count);
  }
}
