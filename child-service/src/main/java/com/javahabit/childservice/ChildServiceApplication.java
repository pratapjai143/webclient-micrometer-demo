package com.javahabit.childservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ChildServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChildServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

    @PostConstruct
    public void init() {
        Hooks.enableAutomaticContextPropagation();
    }
}
