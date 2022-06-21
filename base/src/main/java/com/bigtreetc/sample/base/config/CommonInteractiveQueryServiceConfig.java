package com.bigtreetc.sample.base.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configurable
@Slf4j
public class CommonInteractiveQueryServiceConfig {

  @NonNull final InteractiveQueryService interactiveQueryService;

  @Bean
  RouterFunction<ServerResponse> getStoredValueRoute() {
    return route(
        GET("/queryable-store/{storeName}/{id}"),
        req ->
            ok().contentType(MediaType.APPLICATION_JSON)
                .body(
                    BodyInserters.fromPublisher(
                        Mono.defer(
                            () -> {
                              val storeName = req.pathVariable("storeName");
                              val id = UUID.fromString(req.pathVariable("id"));
                              val storeType = QueryableStoreTypes.<UUID, Object>keyValueStore();
                              val store =
                                  interactiveQueryService.getQueryableStore(storeName, storeType);
                              return Mono.fromCallable(() -> store.get(id));
                            }),
                        Object.class)));
  }
}
