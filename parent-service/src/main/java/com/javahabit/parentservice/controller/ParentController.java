package com.javahabit.parentservice.controller;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ParentController {

    @Autowired
    WebClient webClient;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ObservationRegistry observationRegistry;

    final
    RestTemplate restTemplate;

    public ParentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/parent-web-rd")
    @Observed(
            name = "user.name",
            contextualName = "Parent-->child",
            lowCardinalityKeyValues = {"userType", "userType2"}
    )
    public Mono<String> sayHiParentWebrd(){
        Observation observation = Observation.start("webclient-sample", observationRegistry);
        return Mono.just(observation).flatMap(span -> {
                    observation.scoped(() -> log.info("<ACCEPTANCE_TEST> <TRACE:{} -> SPAN:{}> Hello from consumer",
                            this.tracer.currentSpan().context().traceId(), this.tracer.currentSpan().context().spanId()));
                    return this.webClient.get().uri("/child").retrieve().bodyToMono(String.class);
                })
                .doFinally(signalType -> observation.stop())
                .doOnSuccess(response -> log.info("Response within observation trace {}, span {}", this.tracer.currentSpan().context().traceId() , this.tracer.currentSpan().context().spanId()))
                .contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, observation));
    }

    @GetMapping("/parent-web")
    @Observed(
            name = "user.name",
            contextualName = "Parent-->child",
            lowCardinalityKeyValues = {"userType", "userType2"}
    )
    public Mono<String> sayHiParent(){
        log.info("Parent was called ...");
        log.info("Say Hi to Grandchild ...");
        return webClient.get().uri("/child").retrieve().bodyToMono(String.class).contextCapture();
    }

    @GetMapping("/parent")
    @Observed(
            name = "user.name",
            contextualName = "Parent-->child",
            lowCardinalityKeyValues = {"userType", "userType2"}
    )
    public String sayHi(){
        log.info("Parent was called ...");
        log.info("Say Hi to Grandchild ...");
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:6060/child-service/child",
                HttpMethod.GET,
                null,
                String.class
        );

        log.info("Grandchild response retruned...");
        String responseFromChild = response.getBody();

        return "Grandchild said: ";
    }

    @GetMapping("/parent-iterative")
    @Observed(
            name = "user.name",
            contextualName = "Parent-->child",
            lowCardinalityKeyValues = {"userType", "userType2"}
    )
    public String sayHiMultiple(){
        for(int i =0; i < 5;i++){
            log.info("Parent was called ...");
            log.info("Say Hi to Grandchild ...");
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:6060/child-service/child",
                    HttpMethod.GET,
                    null,
                    String.class
            );

            log.info("Grandchild response retruned...");
            String responseFromChild = response.getBody();
        }

        return "Grandchild said: ";

    }



}
