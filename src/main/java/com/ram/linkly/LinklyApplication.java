package com.ram.linkly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LinklyApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinklyApplication.class, args);
    }

}
