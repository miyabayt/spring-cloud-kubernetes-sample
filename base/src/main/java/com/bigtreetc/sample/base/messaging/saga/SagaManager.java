package com.bigtreetc.sample.base.messaging.saga;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface SagaManager {

  Mono<UUID> startSaga(Event event, Metadata metadata);

  Mono<Void> nextStep(Event event, Metadata metadata);

  Mono<Void> endSaga(Event event, Metadata metadata);

  Mono<SagaResult> waitForComplete(UUID sagaId);
}
