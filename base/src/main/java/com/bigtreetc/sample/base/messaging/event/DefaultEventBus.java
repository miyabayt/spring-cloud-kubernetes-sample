package com.bigtreetc.sample.base.messaging.event;

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
public class DefaultEventBus implements EventBus {

  @NonNull final String topic;

  @NonNull final StreamBridge streamBridge;

  @Override
  public Mono<Boolean> send(EventMessage eventMessage) {
    val id = eventMessage.getId();
    val payloadType = eventMessage.getPayloadType();
    val message =
        MessageBuilder.withPayload(eventMessage).setHeader(KafkaHeaders.MESSAGE_KEY, id).build();
    log.info("sending event message: [id={}, type={}]", id, payloadType);
    return Mono.fromCallable(() -> streamBridge.send(topic, message))
        .doOnSuccess(done -> log.info("event message sent: [id={}, type={}]", id, payloadType));
  }
}
