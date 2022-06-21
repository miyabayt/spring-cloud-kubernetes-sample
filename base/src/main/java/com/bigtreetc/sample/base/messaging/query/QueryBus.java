package com.bigtreetc.sample.base.messaging.query;

import reactor.core.publisher.Mono;

public interface QueryBus {

  Mono<Boolean> send(QueryMessage message);
}
