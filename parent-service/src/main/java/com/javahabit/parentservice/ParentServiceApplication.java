package com.javahabit.parentservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ParentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParentServiceApplication.class, args);
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
