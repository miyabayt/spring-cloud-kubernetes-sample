package com.bigtreetc.sample.base.eventhandling;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import java.util.List;
import reactor.core.publisher.Mono;

public interface EventHandler {

  List<Class<? extends Event>> supports();

  <E extends Event> Mono<Void> handle(E event, Metadata metadata);
}
