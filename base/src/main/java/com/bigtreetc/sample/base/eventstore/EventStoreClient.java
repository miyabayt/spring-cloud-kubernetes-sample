package com.bigtreetc.sample.base.eventstore;

import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class EventStoreClient {

  @NonNull final WebClient webClient;

  public EventStoreClient(WebClient.Builder webClientBuilder, String baseUrl) {
    this.webClient =
        webClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public Flux<EventMessageEntry> getEvents(String aggregateName, UUID aggregateId) {
    val uri = String.format("/events/%s/%s", aggregateName.toLowerCase(), aggregateId);
    return this.webClient.get().uri(uri).retrieve().bodyToFlux(EventMessageEntry.class);
  }

  public Mono<Void> appendEvent(
      String aggregateName, UUID aggregateId, EventMessageEntry eventMessageEntry) {
    return this.appendEvents(aggregateName, aggregateId, List.of(eventMessageEntry));
  }

  public Mono<Void> appendEvents(
      String aggregateName, UUID aggregateId, EventMessageEntry... eventMessageEntries) {
    return this.appendEvents(aggregateName, aggregateId, List.of(eventMessageEntries));
  }

  public Mono<Void> appendEvents(
      String aggregateName, UUID aggregateId, List<EventMessageEntry> eventMessageEntries) {
    val uri = String.format("/events/%s/%s", aggregateName.toLowerCase(), aggregateId);
    return webClient
        .post()
        .uri(uri)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(eventMessageEntries))
        .retrieve()
        .toBodilessEntity()
        .then();
  }
}
