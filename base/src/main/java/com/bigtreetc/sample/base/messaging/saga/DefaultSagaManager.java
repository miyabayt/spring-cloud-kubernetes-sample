package com.bigtreetc.sample.base.messaging.saga;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.QueryableStoreClient;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.time.Duration;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

@RequiredArgsConstructor
@Slf4j
public class DefaultSagaManager implements SagaManager {

  @NonNull final SagaEventBus sagaEventBus;

  @NonNull final QueryableStoreClient queryableStoreClient;

  @Override
  public Mono<UUID> startSaga(Event event, Metadata metadata) {
    log.info(
        "starting saga. [aggregateId={}, evenType={}]",
        event.getAggregateId(),
        event.getClass().getSimpleName());
    return Mono.defer(
        () -> {
          val sagaId = UUID.randomUUID();
          val messageId = UUID.randomUUID();
          val newMetadata = Metadata.from(metadata.getValues()).andSagaId(sagaId);
          val sagaEventMessage =
              SagaEventMessage.builder()
                  .id(messageId)
                  .sagaId(sagaId)
                  .payload(JacksonUtils.writeValueAsString(event))
                  .payloadType(event.getClass().getName())
                  .metadata(JacksonUtils.writeValueAsString(newMetadata))
                  .build();
          return sagaEventBus.send(sagaEventMessage).thenReturn(sagaId);
        });
  }

  @Override
  public Mono<Void> nextStep(Event event, Metadata metadata) {
    log.info(
        "start next step of saga. [aggregateId={}, evenType={}]",
        event.getAggregateId(),
        event.getClass().getSimpleName());
    val sagaId = metadata.getSagaId();
    val messageId = UUID.randomUUID();
    val sagaEventMessage =
        SagaEventMessage.builder()
            .id(messageId)
            .sagaId(sagaId)
            .payload(JacksonUtils.writeValueAsString(event))
            .payloadType(event.getClass().getName())
            .metadata(JacksonUtils.writeValueAsString(metadata))
            .build();
    return sagaEventBus.send(sagaEventMessage).then();
  }

  @Override
  public Mono<Void> endSaga(Event event, Metadata metadata) {
    log.info(
        "ending saga. [aggregateId={}, evenType={}]",
        event.getAggregateId(),
        event.getClass().getSimpleName());
    val sagaId = metadata.getSagaId();
    val messageId = UUID.randomUUID();
    val sagaEventMessage =
        SagaEventMessage.builder()
            .id(messageId)
            .sagaId(sagaId)
            .payload(JacksonUtils.writeValueAsString(event))
            .payloadType(event.getClass().getName())
            .metadata(JacksonUtils.writeValueAsString(metadata))
            .build();
    return sagaEventBus.send(sagaEventMessage).then();
  }

  @Override
  public Mono<SagaResult> waitForComplete(UUID sagaId) {
    log.info("wait for complete saga. [sagaId={}]", sagaId);
    return getSagaResult(sagaId, SagaStatus.COMPLETED, SagaStatus.ABORTED)
        .repeatWhenEmpty(
            Repeat.onlyIf(repeatContext -> true)
                .exponentialBackoff(Duration.ofMillis(25), Duration.ofMillis(500))
                .timeout(Duration.ofSeconds(10)))
        .doOnNext(sagaResult -> log.info("saga processed. {}", sagaResult));
  }

  private Mono<SagaResult> getSagaResult(UUID sagaId, SagaStatus... statuses) {
    return Mono.defer(
        () -> {
          try {
            log.debug("get saga result. [sagaId={}]", sagaId);
            return queryableStoreClient.getStoredValue(SagaResult.class, sagaId);
          } catch (InvalidStateStoreException e) {
            // ignore
          } catch (Exception e) {
            log.warn("failed to get store. [cause={}]", e.getMessage());
          }
          return Mono.empty();
        });
  }
}
