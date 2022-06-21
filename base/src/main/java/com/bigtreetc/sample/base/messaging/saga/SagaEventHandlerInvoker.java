package com.bigtreetc.sample.base.messaging.saga;

import reactor.core.publisher.Mono;

public interface SagaEventHandlerInvoker {

  Mono<Void> invoke(SagaResult sagaResult, SagaEventMessage message);
}
