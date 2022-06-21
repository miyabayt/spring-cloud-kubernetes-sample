package com.bigtreetc.sample.base.messaging.query;

import com.bigtreetc.sample.base.messaging.QueryableStoreClient;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

@RequiredArgsConstructor
@Slf4j
public class DefaultQueryGateway implements QueryGateway {

  @NonNull final QueryBus queryBus;

  @NonNull final QueryableStoreClient queryableStoreClient;

  @Override
  public <A> Mono<A> findOne(Class<A> clazz, Query query) {
    return Mono.defer(
            () -> {
              val queryMessage = query.toQueryMessage();
              return queryBus.send(queryMessage).thenReturn(queryMessage);
            })
        .flatMap(
            q ->
                getQueryResult(q.getId())
                    .repeatWhenEmpty(
                        Repeat.onlyIf(repeatContext -> true)
                            .exponentialBackoff(Duration.ofMillis(25), Duration.ofMillis(500))
                            .timeout(Duration.ofSeconds(10))))
        .map(
            queryResult -> {
              val payload = queryResult.getPayload();
              return JacksonUtils.readValue(payload, clazz);
            });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A> Flux<A> findMany(Class<A> clazz, Query query) {
    return Mono.defer(
            () -> {
              val queryMessage = query.toQueryMessage();
              return queryBus.send(queryMessage).thenReturn(queryMessage);
            })
        .flatMap(
            q ->
                getQueryResult(q.getId())
                    .repeatWhenEmpty(
                        Repeat.onlyIf(repeatContext -> true)
                            .exponentialBackoff(Duration.ofMillis(25), Duration.ofMillis(500))
                            .timeout(Duration.ofSeconds(10))))
        .flatMapMany(
            queryResult -> {
              val javaType =
                  JacksonUtils.getTypeFactory().constructCollectionType(List.class, clazz);
              val payload = queryResult.getPayload();
              val list = (List<A>) JacksonUtils.readValue(payload, javaType);
              return Flux.fromIterable(list);
            });
  }

  private Mono<QueryResult> getQueryResult(UUID messageId) {
    return Mono.defer(
        () -> {
          try {
            log.debug("get query result. [messageId={}]", messageId);
            return queryableStoreClient.getStoredValue(QueryResult.class, messageId);
          } catch (InvalidStateStoreException e) {
            // ignore
          } catch (Exception e) {
            log.warn("failed to get store. [cause={}]", e.getMessage());
          }
          return Mono.empty();
        });
  }
}
