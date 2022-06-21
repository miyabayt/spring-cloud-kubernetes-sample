package com.bigtreetc.sample.base.eventstore;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.model.Aggregate;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface EventSourcingRepository {

  <A extends Aggregate> Mono<A> load(Class<A> clazz, UUID aggregateId);

  <A extends Aggregate> Mono<A> save(A aggregate, Metadata metadata);
}
