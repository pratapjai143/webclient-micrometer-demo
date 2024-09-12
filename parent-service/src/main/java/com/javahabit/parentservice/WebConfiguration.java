package com.javahabit.parentservice;

import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebConfiguration  {

    @Autowired
    Tracer tracer;

    Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    @Autowired
    WebClient.Builder webClientBuilder;

    @Bean
    public WebClient webClient() {
        final WebClient webClient =  webClientBuilder.baseUrl("http://localhost:6060/child-service")
                //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();
        return webClient;
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clinetRequest -> {
            //log.info("Request {} {}", clinetRequest.method(), clinetRequest.url());
            log.info("Request trace {}, span {}", this.tracer.currentSpan().context().traceId() , this.tracer.currentSpan().context().spanId());
            //clinetRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clinetRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clinetResponse -> {
            log.info("Response trace {}, span {}", this.tracer.currentTraceContext().context().traceId() , this.tracer.currentTraceContext().context().spanId());
            //log.info("Response status code {} ", clinetResponse.statusCode());
            //clinetResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clinetResponse);
        });
    }
}
