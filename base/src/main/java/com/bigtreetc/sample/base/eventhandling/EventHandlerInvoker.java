package com.bigtreetc.sample.base.eventhandling;

import com.bigtreetc.sample.base.messaging.event.EventMessage;
import reactor.core.publisher.Mono;

public interface EventHandlerInvoker {

  Mono<Void> invoke(EventMessage message);
}
