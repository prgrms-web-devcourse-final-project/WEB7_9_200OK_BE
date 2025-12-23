package com.windfall.global.initdata;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.entity.AuctionImage;
import com.windfall.domain.auction.entity.AuctionPriceHistory;
import com.windfall.domain.auction.enums.AuctionCategory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionImageRepository;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.chat.entity.ChatImage;
import com.windfall.domain.chat.entity.ChatMessage;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.enums.ChatMessageType;
import com.windfall.domain.chat.repository.ChatImageRepository;
import com.windfall.domain.chat.repository.ChatMessageRepository;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.like.entity.AuctionLike;
import com.windfall.domain.like.repository.AuctionLikeRepository;
import com.windfall.domain.review.entity.Review;
import com.windfall.domain.review.repository.ReviewRepository;
import com.windfall.domain.tag.entity.AuctionTag;
import com.windfall.domain.tag.entity.Tag;
import com.windfall.domain.tag.repository.AuctionTagRepository;
import com.windfall.domain.tag.repository.TagRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.trade.repository.TradeRepository;
import com.windfall.domain.user.entity.User;
import com.windfall.domain.user.enums.ProviderType;
import com.windfall.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LocalSeedDataRunner implements ApplicationRunner {

  private final SeedProperties seedProperties;

  private final UserRepository userRepository;
  private final TagRepository tagRepository;
  private final AuctionTagRepository auctionTagRepository;
  private final AuctionRepository auctionRepository;
  private final AuctionImageRepository auctionImageRepository;
  private final AuctionPriceHistoryRepository auctionPriceHistoryRepository;
  private final AuctionLikeRepository auctionLikeRepository;
  private final TradeRepository tradeRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatImageRepository chatImageRepository;
  private final ReviewRepository reviewRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!seedProperties.enabled()) {
      return;
    }

    boolean alreadySeeded = userRepository.existsByEmail(seedProperties.markerEmail());
    if (alreadySeeded && !seedProperties.reset()) {
      // 이미 들어갔고 reset=false면 아무것도 안 함 (중복 방지)
      return;
    }

    if (seedProperties.reset()) {
      resetAllData();
    }

    SeedContext ctx = new SeedContext(new Random(42));

    seedUsers(ctx);
    seedTags(ctx);
    seedAuctions(ctx);
    seedAuctionImages(ctx);
    seedAuctionTags(ctx);
    seedAuctionPriceHistories(ctx);
    seedAuctionLikes(ctx);

    seedTrades(ctx);
    seedChatRoomsMessagesAndSync(ctx);

    seedReviews(ctx);
  }

  /**
   * FK 순서를 고려해서 “자식부터” 삭제. ddl-auto=update 상태에서도 reset=true로 깔끔하게 재삽입 가능.
   */
  private void resetAllData() {
    chatImageRepository.deleteAllInBatch();
    chatMessageRepository.deleteAllInBatch();
    chatRoomRepository.deleteAllInBatch();

    reviewRepository.deleteAllInBatch();

    auctionLikeRepository.deleteAllInBatch();
    auctionPriceHistoryRepository.deleteAllInBatch();
    auctionTagRepository.deleteAllInBatch();
    auctionImageRepository.deleteAllInBatch();

    tradeRepository.deleteAllInBatch();
    auctionRepository.deleteAllInBatch();

    tagRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  private void seedUsers(SeedContext ctx) {
    User admin = User.builder()
        .provider(ProviderType.GOOGLE)
        .providerUserId("seed-admin-0001")
        .email(seedProperties.markerEmail())
        .nickname("seed_admin")
        .profileImageUrl(ctx.profileImg("admin"))
        .build();

    List<User> users = new ArrayList<>();
    users.add(admin);

    users.add(makeUser(ctx, ProviderType.KAKAO, "sellerA", "sellerA@windfall.local", "판매자A"));
    users.add(makeUser(ctx, ProviderType.KAKAO, "sellerB", "sellerB@windfall.local", "판매자B"));

    users.add(makeUser(ctx, ProviderType.GOOGLE, "buyerA", "buyerA@windfall.local", "구매자A"));
    users.add(makeUser(ctx, ProviderType.GOOGLE, "buyerB", "buyerB@windfall.local", "구매자B"));
    users.add(makeUser(ctx, ProviderType.NAVER, "buyerC", "buyerC@windfall.local", "구매자C"));
    users.add(makeUser(ctx, ProviderType.NAVER, "viewerA", "viewerA@windfall.local", "관전자A"));

    users.add(makeUser(ctx, ProviderType.KAKAO, "userX", "userX@windfall.local", "유저X"));
    users.add(makeUser(ctx, ProviderType.GOOGLE, "userY", "userY@windfall.local", "유저Y"));
    users.add(makeUser(ctx, ProviderType.NAVER, "userZ", "userZ@windfall.local", "유저Z"));

    userRepository.saveAll(users);
    ctx.users = users;
  }

  private User makeUser(SeedContext ctx, ProviderType provider, String providerUserId, String email,
      String nickname) {
    return User.builder()
        .provider(provider)
        .providerUserId("seed-" + providerUserId)
        .email(email)
        .nickname(nickname)
        .profileImageUrl(ctx.profileImg(providerUserId))
        .build();
  }

  private void seedTags(SeedContext ctx) {
    List<String> tagNames = List.of(
        "급처", "미개봉", "정품", "서울직거래", "택배가능",
        "가격내림", "인기", "한정", "새상품", "상태좋음",
        "풀박", "A급", "B급", "세일", "오늘만"
    );

    List<Tag> tags = new ArrayList<>();
    for (String name : tagNames) {
      tags.add(Tag.create(name));
    }

    tagRepository.saveAll(tags);
    ctx.tags = tags;
  }

  private void seedAuctions(SeedContext ctx) {
    // 판매자 2명 고정
    User sellerA = ctx.findUserByEmail("sellerA@windfall.local");
    User sellerB = ctx.findUserByEmail("sellerB@windfall.local");

    LocalDateTime now = LocalDateTime.now();

    List<Auction> auctions = new ArrayList<>();

    // SCHEDULED 2개
    auctions.add(makeAuction(sellerA, "오프화이트 후드티 (S)", "실착 1회, 거의 새상품", AuctionCategory.CLOTHING,
        150_000L, 80_000L, 5_000L, AuctionStatus.SCHEDULED, now.plusHours(3), null));
    auctions.add(
        makeAuction(sellerB, "닌텐도 스위치 OLED", "박스/구성품 풀세트, 스크래치 없음", AuctionCategory.DIGITAL,
            320_000L, 200_000L, 10_000L, AuctionStatus.SCHEDULED, now.plusHours(5), null));

    // PROCESS 6개
    auctions.add(makeAuction(sellerA, "다이슨 에어랩", "구성품 일부 미사용, 상태 A급", AuctionCategory.APPLIANCE,
        450_000L, 450_000L, 250_000L, 10_000L, AuctionStatus.PROCESS, now.minusHours(2), null));
    auctions.add(makeAuction(sellerA, "에어팟 프로 2세대", "미개봉", AuctionCategory.DIGITAL,
        280_000L, 280_000L, 180_000L, 5_000L, AuctionStatus.PROCESS, now.minusHours(1), null));
    auctions.add(
        makeAuction(sellerB, "캠핑 의자 2개 세트", "사용감 약간, 기능 정상", AuctionCategory.SPORTS_LEISURE,
            90_000L, 90_000L, 40_000L, 2_000L, AuctionStatus.PROCESS, now.minusHours(3), null));
    auctions.add(
        makeAuction(sellerB, "원목 책상", "직거래 선호, 생활기스 있음", AuctionCategory.FURNITURE_INTERIOR,
            200_000L, 200_000L, 70_000L, 5_000L, AuctionStatus.PROCESS, now.minusHours(4), null));
    auctions.add(makeAuction(sellerA, "강아지 유모차", "산책용, 접이식", AuctionCategory.PET,
        160_000L, 160_000L, 60_000L, 3_000L, AuctionStatus.PROCESS, now.minusHours(5), null));
    auctions.add(makeAuction(sellerB, "한정판 피규어", "박스 O, 전시만", AuctionCategory.HOBBY,
        120_000L, 120_000L, 60_000L, 2_000L, AuctionStatus.PROCESS, now.minusHours(6), null));

    // COMPLETED 4개
    auctions.add(makeAuction(sellerA, "맥북 에어 M2", "배터리 사이클 적음, 구성품 완비", AuctionCategory.DIGITAL,
        1_200_000L, 800_000L, 20_000L, AuctionStatus.COMPLETED, now.minusDays(2),
        now.minusDays(1).minusHours(2)));
    auctions.add(makeAuction(sellerB, "오프화이트 스니커즈", "정품, 사이즈 270", AuctionCategory.GOODS,
        400_000L, 250_000L, 10_000L, AuctionStatus.COMPLETED, now.minusDays(3),
        now.minusDays(2).minusHours(1)));
    auctions.add(makeAuction(sellerA, "전자레인지", "기능 정상, 외관 사용감", AuctionCategory.APPLIANCE,
        70_000L, 30_000L, 2_000L, AuctionStatus.COMPLETED, now.minusDays(4),
        now.minusDays(3).minusHours(3)));
    auctions.add(makeAuction(sellerB, "도서/티켓 묶음", "도서 5권 + 전시 티켓", AuctionCategory.BOOK_TICKET,
        50_000L, 20_000L, 1_000L, AuctionStatus.COMPLETED, now.minusDays(5),
        now.minusDays(4).minusHours(4)));

    auctionRepository.saveAll(auctions);
    ctx.auctions = auctions;
  }

  private Auction makeAuction(
      User seller,
      String title,
      String description,
      AuctionCategory category,
      Long startPrice,
      Long stopLoss,
      Long dropAmount,
      AuctionStatus status,
      LocalDateTime startedAt,
      LocalDateTime endedAt
  ) {
    return Auction.builder()
        .seller(seller)
        .title(title)
        .description(description)
        .category(category)
        .startPrice(startPrice)
        .currentPrice(startPrice)
        .stopLoss(stopLoss)
        .dropAmount(dropAmount)
        .status(status)
        .startedAt(startedAt)
        .endedAt(endedAt)
        .build();
  }

  private Auction makeAuction(
      User seller,
      String title,
      String description,
      AuctionCategory category,
      Long startPrice,
      Long currentPrice,
      Long stopLoss,
      Long dropAmount,
      AuctionStatus status,
      LocalDateTime startedAt,
      LocalDateTime endedAt
  ) {
    return Auction.builder()
        .seller(seller)
        .title(title)
        .description(description)
        .category(category)
        .startPrice(startPrice)
        .currentPrice(currentPrice) // SCHEDULED는 displayPrice가 startPrice지만, 저장은 해도 무방
        .stopLoss(stopLoss)
        .dropAmount(dropAmount)
        .status(status)
        .startedAt(startedAt)
        .endedAt(endedAt)
        .build();
  }

  private void seedAuctionImages(SeedContext ctx) {
    List<AuctionImage> images = new ArrayList<>();
    long size = 180_000L;

    for (Auction auction : ctx.auctions) {
      String seedKey = "auction-" + auction.getTitle().hashCode();
      images.add(
          AuctionImage.builder().auction(auction).image(ctx.img(seedKey + "-1", 800)).size(size)
              .build());
      images.add(
          AuctionImage.builder().auction(auction).image(ctx.img(seedKey + "-2", 800)).size(size)
              .build());
      images.add(
          AuctionImage.builder().auction(auction).image(ctx.img(seedKey + "-3", 800)).size(size)
              .build());
    }

    auctionImageRepository.saveAll(images);
    ctx.auctionImages = images;
  }

  private void seedAuctionTags(SeedContext ctx) {
    List<AuctionTag> links = new ArrayList<>();

    for (Auction auction : ctx.auctions) {
      // 경매당 태그 2~3개
      int tagCount = 2 + ctx.rnd.nextInt(2); // 2 or 3
      Set<Tag> picked = new LinkedHashSet<>();
      while (picked.size() < tagCount) {
        picked.add(ctx.tags.get(ctx.rnd.nextInt(ctx.tags.size())));
      }
      for (Tag tag : picked) {
        links.add(AuctionTag.create(auction, tag));
      }
    }

    auctionTagRepository.saveAll(links);
    ctx.auctionTags = links;
  }

  private void seedAuctionPriceHistories(SeedContext ctx) {
    List<AuctionPriceHistory> histories = new ArrayList<>();

    for (Auction auction : ctx.auctions) {
      if (auction.getStatus() != AuctionStatus.PROCESS) {
        continue;
      }

      long base = auction.getStartPrice();
      long viewer = 10 + ctx.rnd.nextInt(50);

      // 5개 히스토리
      for (int i = 0; i < 5; i++) {
        long price = Math.max(0, base - (auction.getDropAmount() * i));
        long viewerCount = viewer + (i * (5 + ctx.rnd.nextInt(10)));
        histories.add(AuctionPriceHistory.create(auction, price, viewerCount));
        // 마지막 price를 currentPrice로 동기화
        if (i == 4) {
          auction.updateCurrentPrice(price);
        }
      }
    }

    // currentPrice 업데이트
    auctionRepository.saveAll(ctx.auctions);
    auctionPriceHistoryRepository.saveAll(histories);
    ctx.priceHistories = histories;
  }

  private void seedAuctionLikes(SeedContext ctx) {
    List<AuctionLike> likes = new ArrayList<>();
    List<User> users = ctx.users;

    for (Auction auction : ctx.auctions) {
      // 경매당 0~4개
      int likeCount = ctx.rnd.nextInt(5);

      Set<Long> likedUserIds = new HashSet<>();
      for (int i = 0; i < likeCount; i++) {
        User u = users.get(ctx.rnd.nextInt(users.size()));
        Long userId = u.getId();

        if (userId == null || likedUserIds.contains(userId)) {
          continue;
        }
        likedUserIds.add(userId);

        likes.add(AuctionLike.create(auction, userId));
      }
    }

    auctionLikeRepository.saveAll(likes);
    ctx.likes = likes;
  }

  private void seedTrades(SeedContext ctx) {
    // COMPLETED 경매 4개에 대해 각각 Trade 1개씩
    List<Auction> completed = ctx.auctions.stream()
        .filter(a -> a.getStatus() == AuctionStatus.COMPLETED)
        .toList();

    User buyerA = ctx.findUserByEmail("buyerA@windfall.local");
    User buyerB = ctx.findUserByEmail("buyerB@windfall.local");
    User buyerC = ctx.findUserByEmail("buyerC@windfall.local");

    List<Trade> trades = new ArrayList<>();

    // 상태 다양화 (PAYMENT_COMPLETED, PURCHASE_CONFIRMED는 채팅방 생성 케이스로 사용)
    trades.add(makeTrade(completed.get(0), buyerA.getId(), completed.get(0).getSeller().getId(),
        TradeStatus.PAYMENT_COMPLETED, completed.get(0).getCurrentPrice()));
    trades.add(makeTrade(completed.get(1), buyerB.getId(), completed.get(1).getSeller().getId(),
        TradeStatus.PURCHASE_CONFIRMED, completed.get(1).getCurrentPrice()));
    trades.add(makeTrade(completed.get(2), buyerC.getId(), completed.get(2).getSeller().getId(),
        TradeStatus.PAYMENT_CANCELED, completed.get(2).getCurrentPrice()));
    trades.add(makeTrade(completed.get(3), buyerA.getId(), completed.get(3).getSeller().getId(),
        TradeStatus.PAYMENT_FAILED, completed.get(3).getCurrentPrice()));

    tradeRepository.saveAll(trades);
    ctx.trades = trades;
  }

  private Trade makeTrade(Auction auction, Long buyerId, Long sellerId, TradeStatus status,
      Long finalPrice) {
    return Trade.builder()
        .auction(auction)
        .buyerId(buyerId)
        .sellerId(sellerId)
        .status(status)
        .finalPrice(finalPrice)
        .build();
  }

  private void seedChatRoomsMessagesAndSync(SeedContext ctx) {
    List<Trade> chatEligible = ctx.trades.stream()
        .filter(t -> t.getStatus() == TradeStatus.PAYMENT_COMPLETED
            || t.getStatus() == TradeStatus.PURCHASE_CONFIRMED)
        .toList();

    List<ChatRoom> rooms = new ArrayList<>();
    for (Trade trade : chatEligible) {
      rooms.add(ChatRoom.builder().trade(trade).build());
    }
    chatRoomRepository.saveAll(rooms);
    ctx.chatRooms = rooms;

    // 방마다 메시지 14개 만들고, 이미지 메시지에는 ChatImage 1개씩
    List<ChatMessage> allMessages = new ArrayList<>();
    List<ChatImage> allChatImages = new ArrayList<>();

    for (ChatRoom room : rooms) {
      Trade trade = room.getTrade();
      User buyer = ctx.findUserById(trade.getBuyerId());
      User seller = ctx.findUserById(trade.getSellerId());

      // SYSTEM 1개 (첫 메시지)
      allMessages.add(makeMessage(room, seller,
          "거래 채팅이 생성되었습니다. 안전 거래를 진행해 주세요.", ChatMessageType.SYSTEM, true));

      // TEXT 11개 + IMAGE 2개 (총 14개)
      for (int i = 1; i <= 11; i++) {
        User sender = (i % 2 == 0) ? buyer : seller;
        allMessages.add(
            makeMessage(room, sender, makeChatText(trade, i), ChatMessageType.TEXT, i < 6));
      }

      ChatMessage img1 = makeMessage(room, buyer, "사진을 보냈습니다.", ChatMessageType.IMAGE, false);
      ChatMessage img2 = makeMessage(room, seller, "상태 확인용 사진입니다.", ChatMessageType.IMAGE, false);
      allMessages.add(img1);
      allMessages.add(img2);

    }

    chatMessageRepository.saveAll(allMessages);

    // IMAGE 타입만 골라서 ChatImage 저장
    for (ChatMessage m : allMessages) {
      if (m.getMessageType() != ChatMessageType.IMAGE) {
        continue;
      }
      String seedKey = "chat-" + m.getId();
      allChatImages.add(ChatImage.builder()
          .chatMessage(m)
          .imageUrl(ctx.img(seedKey, 600))
          .build());
    }
    chatImageRepository.saveAll(allChatImages);

    // 마지막 메시지 기준으로 ChatRoom last* 동기화
    syncChatRoomLastMessage(rooms);
  }

  private ChatMessage makeMessage(ChatRoom room, User sender, String content, ChatMessageType type,
      boolean isRead) {
    return ChatMessage.builder()
        .chatRoom(room)
        .sender(sender)
        .content(content)
        .messageType(type)
        .isRead(isRead)
        .build();
  }

  private void syncChatRoomLastMessage(List<ChatRoom> rooms) {
    for (ChatRoom room : rooms) {

      ChatMessage last = chatMessageRepository.findTopByChatRoomIdOrderByCreateDateDesc(
          room.getId());

      String preview = last.getContent();
      if (preview != null && preview.length() > 200) {
        preview = preview.substring(0, 200);
      }

      room.updateLastMessage(last.getCreateDate(), preview, last.getMessageType());
    }
    chatRoomRepository.saveAll(rooms);
  }

  private void seedReviews(SeedContext ctx) {
    // PURCHASE_CONFIRMED Trade에만 1개
    Trade confirmed = ctx.trades.stream()
        .filter(t -> t.getStatus() == TradeStatus.PURCHASE_CONFIRMED)
        .findFirst()
        .orElse(null);

    if (confirmed == null) {
      return;
    }

    Review review = Review.createReview(
        confirmed,
        5,
        "상품 상태가 설명과 동일하고, 응대도 빨랐습니다. 다음에도 거래하고 싶어요!"
    );

    reviewRepository.save(review);
    ctx.review = review;
  }

  private String makeChatText(Trade trade, int idx) {
    return switch (idx) {
      case 1 -> "안녕하세요! 상품 아직 거래 가능할까요?";
      case 2 -> "네 가능합니다. 배송 원하시나요, 직거래 원하시나요?";
      case 3 -> "택배로 부탁드릴게요. 포장 꼼꼼히 가능할까요?";
      case 4 -> "네, 뽁뽁이+박스로 안전하게 포장해드릴게요.";
      case 5 -> "좋습니다. 결제 완료되면 주소 공유드릴게요.";
      case 6 -> "확인했습니다. 송장 나오면 바로 알려드릴게요.";
      case 7 -> "감사합니다! 혹시 구성품 빠진 건 없죠?";
      case 8 -> "네, 구성품 모두 포함입니다. 사진도 곧 보내드릴게요.";
      case 9 -> "좋아요. 상태 확인 후 구매 확정할게요.";
      case 10 -> "넵, 도착하면 바로 확인 부탁드려요!";
      case 11 -> "받았습니다. 상태 아주 좋네요. 구매 확정하겠습니다.";
      default -> "확인했습니다.";
    };
  }

  private static class SeedContext {

    private final Random rnd;

    private List<User> users = List.of();
    private List<Tag> tags = List.of();
    private List<Auction> auctions = List.of();
    private List<AuctionImage> auctionImages = List.of();
    private List<AuctionTag> auctionTags = List.of();
    private List<AuctionPriceHistory> priceHistories = List.of();
    private List<AuctionLike> likes = List.of();
    private List<Trade> trades = List.of();
    private List<ChatRoom> chatRooms = List.of();
    private Review review;

    private SeedContext(Random rnd) {
      this.rnd = rnd;
    }

    User findUserByEmail(String email) {
      return users.stream().filter(u -> u.getEmail().equals(email)).findFirst()
          .orElseThrow(() -> new IllegalStateException("Seed user not found: " + email));
    }

    User findUserById(Long id) {
      return users.stream().filter(u -> Objects.equals(u.getId(), id)).findFirst()
          .orElseThrow(() -> new IllegalStateException("Seed userId not found: " + id));
    }

    String img(String seed, int size) {
      // 임시 이미지
      return "https://picsum.photos/seed/" + seed + "/" + size + "/" + size;
    }

    String profileImg(String seed) {
      return "https://picsum.photos/seed/profile-" + seed + "/300/300";
    }
  }
}

