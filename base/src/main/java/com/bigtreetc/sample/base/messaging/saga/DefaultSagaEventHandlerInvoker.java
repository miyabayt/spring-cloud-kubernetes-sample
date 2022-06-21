package com.bigtreetc.sample.base.messaging.saga;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.utils.ClassUtils;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultSagaEventHandlerInvoker implements SagaEventHandlerInvoker {

  @NonNull final SagaEventHandlerResolver sagaEventHandlerResolver;

  @Override
  public Mono<Void> invoke(SagaResult sagaResult, SagaEventMessage sagaEventMessage) {
    val id = sagaEventMessage.getId();
    val sagaId = sagaEventMessage.getSagaId();
    val eventType = sagaEventMessage.getPayloadType();
    val handler = sagaEventHandlerResolver.resolve(eventType);

    if (handler == null) {
      log.info("no handler found: [id={}, eventType={}]", id, eventType);
      return Mono.empty();
    }

    log.info(
        "handle saga event message: [id={}, sagaId={}, eventType={}, handler={}]",
        id,
        sagaId,
        eventType,
        handler);
    val payload = sagaEventMessage.getPayload();
    val event = JacksonUtils.readValue(payload, ClassUtils.getClass(Event.class, eventType));
    val metadata = JacksonUtils.readValue(sagaEventMessage.getMetadata(), Metadata.class);
    val newMetadata = Metadata.from(metadata.getValues()).andSagaId(sagaId);

    if (sagaResult.getSagaSteps().stream()
        .noneMatch(step -> Objects.equals(step.getEventType(), eventType))) {
      sagaResult.addSagaStep(event, SagaStepStatus.STARTED);
    }

    return handler
        .handle(event, newMetadata)
        .doOnError(
            e -> {
              log.warn("failed to handle saga event.", e);
              sagaResult.setSagaStepStatus(event, SagaStepStatus.FAILED);
            })
        .doOnSuccess(done -> sagaResult.setSagaStepStatus(event, SagaStepStatus.COMPLETED));
  }
}
