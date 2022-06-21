package com.bigtreetc.sample.base.eventstore;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventSourcingStorage {

  Flux<EventMessageEntry> find(String aggregateName, UUID aggregateId);

  Mono<Void> save(String aggregateName, Event event, Metadata metadata);
}
