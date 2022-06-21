package com.bigtreetc.sample.base.messaging.event;

import reactor.core.publisher.Mono;

public interface EventBus {

  Mono<Boolean> send(EventMessage message);
}
