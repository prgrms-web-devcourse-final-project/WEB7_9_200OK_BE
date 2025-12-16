package com.windfall.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

  @PersistenceContext
  private final EntityManager em;

  @Bean
  public JPAQueryFactory queryFactory(){
    return new JPAQueryFactory(em);
  }
}
