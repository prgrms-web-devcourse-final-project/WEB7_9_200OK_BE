package com.windfall.domain.tag.repository;

import com.windfall.domain.tag.entity.AuctionTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionTagRepository extends JpaRepository<AuctionTag, Long> {

}