package com.windfall.domain.chat.repository;

import com.windfall.domain.chat.entity.ChatImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {

}
