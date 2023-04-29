package com.bigtreetc.sample.base.messaging.query;

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
public class DefaultQueryBus implements QueryBus {

  @NonNull final String topic;

  @NonNull final StreamBridge streamBridge;

  @Override
  public Mono<Boolean> send(QueryMessage queryMessage) {
    val id = queryMessage.getId();
    val payloadType = queryMessage.getPayloadType();
    val message = MessageBuilder.withPayload(queryMessage).setHeader(KafkaHeaders.KEY, id).build();
    log.info("sending query message: [id={}, type={}]", id, payloadType);
    return Mono.fromCallable(() -> streamBridge.send(topic, message))
        .doOnSuccess(done -> log.info("query message sent: [id={}, type={}]", id, payloadType));
  }
}
