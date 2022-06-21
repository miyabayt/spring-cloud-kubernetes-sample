package com.bigtreetc.sample.eventstore.domain.repository;

import com.bigtreetc.sample.eventstore.domain.model.EventEntity;
import com.bigtreetc.sample.eventstore.domain.model.SnapshotEntity;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository<E extends EventEntity, S extends SnapshotEntity> {

  Mono<Void> createTables(String aggregate);

  default Flux<E> findEvents(String aggregateName, UUID aggregateId) {
    return findEvents(aggregateName, aggregateId, -1);
  }

  Flux<E> findEvents(String aggregateName, UUID aggregateId, long baseSequence);

  Mono<E> saveEvent(String aggregateName, E event);

  Mono<S> findSnapshot(String aggregateName, UUID aggregateId);

  Mono<S> saveSnapshot(String aggregateName, S snapshot);
}
