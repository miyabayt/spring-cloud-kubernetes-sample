package com.bigtreetc.sample.base.messaging;

import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class QueryableStoreClient {

  @NonNull final InteractiveQueryService interactiveQueryService;

  @NonNull final WebClient.Builder webClientBuilder;

  private WebClient webClient;

  public <T> Mono<T> getStoredValue(Class<T> clazz, UUID messageId) {
    val hostInfo =
        interactiveQueryService.getHostInfo(clazz.getSimpleName(), messageId, new UUIDSerializer());
    log.debug("key located in: {}", hostInfo);
    if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {
      val storeType = QueryableStoreTypes.<UUID, T>keyValueStore();
      val store = interactiveQueryService.getQueryableStore(clazz.getSimpleName(), storeType);
      val result = store.get(messageId);
      return Mono.justOrEmpty(result);
    } else if (hostInfo.port() > 0) {
      val uri =
          String.format(
              "%s:%d/queryable-store/%s/%s",
              hostInfo.host(), hostInfo.port(), clazz.getSimpleName(), messageId);
      return this.webClient.get().uri(uri).retrieve().bodyToMono(clazz);
    }
    return Mono.empty();
  }

  @PostConstruct
  private void createWebClient() {
    this.webClient =
        WebClient.builder()
            .defaultHeader("Content-Type", new String[] {"application/json"})
            .build();
  }
}
