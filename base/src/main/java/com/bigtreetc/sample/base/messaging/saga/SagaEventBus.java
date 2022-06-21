package com.bigtreetc.sample.base.messaging.saga;

import reactor.core.publisher.Mono;

public interface SagaEventBus {

  Mono<Boolean> send(SagaEventMessage message);
}
