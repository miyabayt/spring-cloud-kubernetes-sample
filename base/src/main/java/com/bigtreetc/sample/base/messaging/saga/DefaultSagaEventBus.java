package com.bigtreetc.sample.base.messaging.saga;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultSagaEventBus implements SagaEventBus {

  @NonNull final String topic;

  @NonNull final StreamBridge streamBridge;

  @Override
  public Mono<Boolean> send(SagaEventMessage eventMessage) {
    val id = eventMessage.getId();
    val sagaId = eventMessage.getSagaId();
    val payloadType = eventMessage.getPayloadType();
    val message = MessageBuilder.withPayload(eventMessage).setHeader(KafkaHeaders.KEY, id).build();
    log.info("sending saga event message: [id={}, sagaId={}, type={}]", id, sagaId, payloadType);
    return Mono.fromCallable(() -> streamBridge.send(topic, message))
        .doOnSuccess(
            done ->
                log.info(
                    "saga event message sent: [id={}, sagaId={}, type={}]",
                    id,
                    sagaId,
                    payloadType));
  }
}
