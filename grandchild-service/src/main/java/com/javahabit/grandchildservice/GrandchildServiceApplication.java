package com.javahabit.grandchildservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class GrandchildServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrandchildServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        Hooks.enableAutomaticContextPropagation();
    }
}
