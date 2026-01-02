package com.windfall.domain.mypage.repository;

import com.windfall.api.mypage.dto.dashboard.DashBoardCalenderDTO;
import com.windfall.domain.auction.entity.Auction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashBoardRepository extends JpaRepository<Auction, Long> {

  @Query("""
	SELECT
	new com.windfall.api.mypage.dto.dashboard.DashBoardCalenderDTO(
	  CAST(a.startedAt AS localdate),
    CAST(COALESCE(SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END), 0) AS int),
    CAST(COALESCE(SUM(CASE WHEN a.status = 'PROCESS' THEN 1 ELSE 0 END), 0) AS int)
    )
    FROM Auction a
    WHERE a.startedAt BETWEEN :startDate AND :endDate
	GROUP BY CAST(a.startedAt AS localdate) ORDER BY CAST(a.startedAt AS localdate)
""")
  List<DashBoardCalenderDTO> findDashBoardAuctions(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


}
