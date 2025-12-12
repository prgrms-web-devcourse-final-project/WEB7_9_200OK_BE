package com.windfall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WindfallApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindfallApplication.class, args);
    }

}
