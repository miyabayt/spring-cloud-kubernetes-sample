package com.bigtreetc.sample.base.messaging.query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface QueryGateway {

  <A> Mono<A> findOne(Class<A> clazz, Query query);

  <A> Flux<A> findMany(Class<A> clazz, Query query);
}
