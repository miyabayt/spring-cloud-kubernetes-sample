package com.bigtreetc.sample.base.eventstore;

import com.bigtreetc.sample.base.exception.NoDataFoundException;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.model.Aggregate;
import com.bigtreetc.sample.base.utils.ReflectionUtils;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultEventSourcingRepository implements EventSourcingRepository {

  @NonNull final EventSourcingStorage eventSourcingStorage;

  @Override
  public <A extends Aggregate> Mono<A> load(Class<A> clazz, UUID aggregateId) {
    val aggregateName = clazz.getSimpleName();
    return eventSourcingStorage
        .find(aggregateName, aggregateId)
        .switchIfEmpty(Mono.error(new NoDataFoundException()))
        .collectList()
        .map(
            eventMessageEntries -> {
              val aggregate = ReflectionUtils.newInstance(clazz);
              aggregate.load(eventMessageEntries);
              return aggregate;
            });
  }

  @Override
  public <A extends Aggregate> Mono<A> save(A aggregate, Metadata metadata) {
    if (aggregate.hasUncommittedEvents()) {
      val uncommittedEvents = aggregate.getUncommittedEvents();
      return Flux.fromIterable(uncommittedEvents)
          .concatMap(
              event -> {
                val aggregateName = aggregate.getClass().getSimpleName();
                return eventSourcingStorage.save(aggregateName, event, metadata);
              })
          .then()
          .doOnError(e -> log.error("failed to store event.", e))
          .doOnSuccess(done -> aggregate.flushUncommittedEvents())
          .thenReturn(aggregate);
    }
    return Mono.empty();
  }
}
