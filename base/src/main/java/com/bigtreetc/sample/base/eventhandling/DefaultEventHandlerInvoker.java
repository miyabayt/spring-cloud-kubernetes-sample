package com.bigtreetc.sample.base.eventhandling;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.messaging.event.EventMessage;
import com.bigtreetc.sample.base.utils.ClassUtils;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultEventHandlerInvoker implements EventHandlerInvoker {

  @NonNull final EventHandlerResolver eventHandlerResolver;

  @Override
  public Mono<Void> invoke(EventMessage eventMessage) {
    val id = eventMessage.getId();
    val eventType = eventMessage.getPayloadType();
    val handler = eventHandlerResolver.resolve(eventType);

    if (handler == null) {
      log.info("no handler found: [id={}, eventType={}]", id, eventType);
      return Mono.empty();
    }

    log.info("handle event message: [id={}, eventType={}, handler={}]", id, eventType, handler);
    val payload = eventMessage.getPayload();
    val event = JacksonUtils.readValue(payload, ClassUtils.getClass(Event.class, eventType));
    val metadata = JacksonUtils.readValue(eventMessage.getMetadata(), Metadata.class);

    return handler.handle(event, metadata);
  }
}
