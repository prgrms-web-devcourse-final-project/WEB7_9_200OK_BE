package com.windfall.global.redis;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Redis Pub/Sub content & destination DTO")
public record RedisPubSubMessage(
    String destination,
    Object content
) {

}