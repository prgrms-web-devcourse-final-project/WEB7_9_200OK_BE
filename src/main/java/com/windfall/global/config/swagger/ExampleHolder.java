package com.windfall.global.config.swagger;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.models.examples.Example;
import org.springframework.http.HttpStatus;

@Getter
public class ExampleHolder {
    private final Example holder;
    private final HttpStatus code;
    private final String name;

    @Builder
    private ExampleHolder(Example holder, HttpStatus code, String name) {
        this.holder = holder;
        this.code = code;
        this.name = name;
    }
}
