package com.bigtreetc.sample.base.messaging.command;

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
public class DefaultCommandBus implements CommandBus {

  @NonNull final String topic;

  @NonNull final StreamBridge streamBridge;

  @Override
  public Mono<Boolean> send(CommandMessage commandMessage) {
    val id = commandMessage.getId();
    val payloadType = commandMessage.getPayloadType();
    val message =
        MessageBuilder.withPayload(commandMessage).setHeader(KafkaHeaders.MESSAGE_KEY, id).build();
    log.info("sending command message: [id={}, type={}]", id, payloadType);
    return Mono.fromCallable(() -> streamBridge.send(topic, message))
        .doOnSuccess(done -> log.info("command message sent: [id={}, type={}]", id, payloadType));
  }
}
