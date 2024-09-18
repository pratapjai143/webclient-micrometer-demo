package com.javahabit.parentservice;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

//@Component
public class ResponseLoggingCustomizer implements WebClientCustomizer {

    @Autowired
    Tracer tracer;

    private static final Logger logger = LoggerFactory.getLogger(ResponseLoggingCustomizer.class);

    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.filter(logResponse());
        webClientBuilder.filter(logRequest());
        webClientBuilder.defaultHeader("User-Agent", "MY-APPLICATION");

    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            return Mono.defer(() -> {
                logger.info("Response: {}", clientResponse.statusCode());
                logger.info("--- Http Headers of Response: ---");
                clientResponse.headers().asHttpHeaders()
                        .forEach((name, values) -> values.forEach(value -> logger.info("{}={}", name, value)));
                return Mono.just(clientResponse);
            });

            //return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            Span parentSpan = tracer.currentSpan();
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            logger.info("--- Http Headers of Request: ---");
            clientRequest.headers().forEach(this::logHeader);
            return next.exchange(clientRequest);
        };
    }

    private void logHeader(String name, List<String> values) {
        values.forEach(value -> logger.info("{}={}", name, value));
    }
}
