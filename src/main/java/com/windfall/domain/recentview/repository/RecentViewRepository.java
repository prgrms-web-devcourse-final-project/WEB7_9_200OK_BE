package com.windfall.domain.recentview.repository;

import com.windfall.domain.recentview.entity.RecentView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentViewRepository extends JpaRepository<RecentView, Long> {

}
