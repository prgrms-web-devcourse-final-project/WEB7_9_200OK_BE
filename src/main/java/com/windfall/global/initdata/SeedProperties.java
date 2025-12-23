package com.windfall.global.initdata;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(
    boolean enabled,
    boolean reset,
    String markerEmail
) {}
