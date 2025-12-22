package com.windfall.global.config;

import com.windfall.global.initdata.SeedProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SeedProperties.class)
public class SeedConfig {
}
