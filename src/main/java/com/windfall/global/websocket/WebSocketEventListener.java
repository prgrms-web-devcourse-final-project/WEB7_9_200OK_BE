package com.windfall.global.websocket;

import com.windfall.api.auction.dto.response.message.AuctionViewerMessage;
import com.windfall.api.auction.service.AuctionViewerService;
import com.windfall.api.auction.service.component.AuctionMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final AuctionMessageSender messageSender;
  private final AuctionViewerService viewerService;

  @EventListener
  public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String destination = headerAccessor.getDestination();
    String sessionId = headerAccessor.getSessionId();

    if (destination == null || !destination.startsWith("/topic/auction/")) {
      return;
    }

    Long auctionId;
    try {
      auctionId = Long.parseLong(destination.replace("/topic/auction/", ""));
    } catch (NumberFormatException e) {
      log.warn("Invalid auction ID in destination: {}", destination);
      return;
    }

    long currentCount = viewerService.addViewer(auctionId, sessionId);
    messageSender.broadcastViewerCount(auctionId, currentCount);
    log.info("Broadcast auctionId : {}, viewer count: {}", auctionId, currentCount);
  }

  @EventListener
  public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    Long auctionId = viewerService.removeViewer(sessionId);

    if (auctionId == null) {
      return;
    }

    long currentCount = viewerService.getViewerCount(auctionId);
    messageSender.broadcastViewerCount(auctionId, currentCount);
    log.info("Broadcast auctionId : {}, viewer count: {}", auctionId, currentCount);
  }
}
