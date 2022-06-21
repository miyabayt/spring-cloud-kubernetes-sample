package com.bigtreetc.sample.base.config;

import com.bigtreetc.sample.base.commandhandling.CommandHandlerInvoker;
import com.bigtreetc.sample.base.eventhandling.EventHandlerInvoker;
import com.bigtreetc.sample.base.messaging.command.*;
import com.bigtreetc.sample.base.messaging.event.EventMessage;
import com.bigtreetc.sample.base.messaging.event.EventResult;
import com.bigtreetc.sample.base.messaging.query.QueryMessage;
import com.bigtreetc.sample.base.messaging.query.QueryResult;
import com.bigtreetc.sample.base.messaging.saga.*;
import com.bigtreetc.sample.base.queryhandling.QueryHandlerInvoker;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class CommonStreamsConfig {

  @Bean
  public Serde<CommandMessage> commandMessageSerde() {
    return new JsonSerde<>(CommandMessage.class);
  }

  @Bean
  public Serde<QueryMessage> queryMessageSerde() {
    return new JsonSerde<>(QueryMessage.class);
  }

  @Bean
  public Serde<EventMessage> eventMessageSerde() {
    return new JsonSerde<>(EventMessage.class);
  }

  @Bean
  public Serde<CommandResult> commandResultSerde() {
    return new JsonSerde<>(CommandResult.class);
  }

  @Bean
  public Serde<QueryResult> queryResultSerde() {
    return new JsonSerde<>(QueryResult.class);
  }

  @Bean
  public Serde<EventResult> eventResultSerde() {
    return new JsonSerde<>(EventResult.class);
  }

  @Bean
  public Serde<SagaEventMessage> sagaEventMessageSerde() {
    return new JsonSerde<>(SagaEventMessage.class);
  }

  @Bean
  public Serde<SagaResult> sagaResultSerde() {
    return new JsonSerde<>(SagaResult.class);
  }

  @Bean
  public Function<KStream<UUID, CommandMessage>, KTable<UUID, CommandResult>> commandReceiver(
      CommandHandlerInvoker commandHandlerInvoker,
      Serde<CommandMessage> commandMessageSerde,
      Serde<CommandResult> commandResultSerde) {
    return (kStream) ->
        kStream
            .groupBy(
                (messageId, commandMessage) -> commandMessage.getId(),
                Grouped.with(null, commandMessageSerde))
            .aggregate(
                CommandResult::new,
                (messageId, commandMessage, result) -> {
                  log.debug(
                      "command message received. [id={}, commandType={}]",
                      commandMessage.getId(),
                      commandMessage.getPayloadType());
                  result.setAggregateId(commandMessage.getSourceAggregateId());
                  result.setMetadata(commandMessage.getMetadata());
                  try {
                    commandHandlerInvoker.invoke(commandMessage).block();
                    result.setPayload(commandMessage.getPayload());
                    result.setPayloadType(commandMessage.getPayloadType());
                  } catch (Exception e) {
                    log.error("failed to invoke.", e);
                    // TODO
                    result.setPayload(e.getMessage());
                    result.setPayloadType(e.getClass().getName());
                  }
                  return result;
                },
                Materialized.<UUID, CommandResult, KeyValueStore<Bytes, byte[]>>as(
                        CommandResult.class.getSimpleName())
                    .withKeySerde(Serdes.UUID())
                    .withValueSerde(commandResultSerde));
  }

  @Bean
  public Function<KStream<UUID, EventMessage>, KTable<UUID, EventResult>> eventReceiver(
      EventHandlerInvoker eventHandlerInvoker,
      Serde<EventMessage> eventMessageSerde,
      Serde<EventResult> eventResultSerde) {
    return (kStream) ->
        kStream
            .groupBy(
                (messageId, eventMessage) -> eventMessage.getId(),
                Grouped.with(null, eventMessageSerde))
            .aggregate(
                EventResult::new,
                (messageId, eventMessage, result) -> {
                  log.debug(
                      "event message received. [id={}, eventType={}]",
                      eventMessage.getId(),
                      eventMessage.getPayloadType());
                  result.setAggregateId(eventMessage.getSourceAggregateId());
                  result.setMetadata(eventMessage.getMetadata());
                  try {
                    eventHandlerInvoker.invoke(eventMessage).block();
                    result.setPayload(eventMessage.getPayload());
                    result.setPayloadType(eventMessage.getPayloadType());
                  } catch (Exception e) {
                    // TODO
                    result.setPayload(e.getMessage());
                    result.setPayloadType(e.getClass().getName());
                  }
                  return result;
                },
                Materialized.<UUID, EventResult, KeyValueStore<Bytes, byte[]>>as(
                        EventResult.class.getSimpleName())
                    .withKeySerde(Serdes.UUID())
                    .withValueSerde(eventResultSerde));
  }

  @Bean
  public Function<KStream<UUID, QueryMessage>, KTable<UUID, QueryResult>> queryReceiver(
      QueryHandlerInvoker queryHandlerInvoker,
      Serde<QueryMessage> queryMessageSerde,
      Serde<QueryResult> queryResultSerde) {
    return (kStream) ->
        kStream
            .groupBy(
                (messageId, queryMessage) -> queryMessage.getId(),
                Grouped.with(null, queryMessageSerde))
            .aggregate(
                QueryResult::new,
                (messageId, queryMessage, result) -> {
                  log.debug(
                      "query message received. [id={}, queryType={}]",
                      queryMessage.getId(),
                      queryMessage.getPayloadType());
                  try {
                    val found = queryHandlerInvoker.invoke(queryMessage);
                    if (found instanceof Mono<?> mono) {
                      val aggregate = mono.block();
                      result.setPayload(JacksonUtils.writeValueAsString(aggregate));
                    } else if (found instanceof Flux<?> flux) {
                      val aggregates = flux.collectList().block();
                      result.setPayload(JacksonUtils.writeValueAsString(aggregates));
                    }
                    result.setPayloadType(queryMessage.getPayloadType());
                  } catch (Exception e) {
                    // TODO
                    result.setPayload(e.getMessage());
                    result.setPayloadType(e.getClass().getName());
                  }
                  return result;
                },
                Materialized.<UUID, QueryResult, KeyValueStore<Bytes, byte[]>>as(
                        QueryResult.class.getSimpleName())
                    .withKeySerde(Serdes.UUID())
                    .withValueSerde(queryResultSerde));
  }

  @Bean
  public Function<KStream<UUID, SagaEventMessage>, KTable<UUID, SagaResult>> sagaEventReceiver(
      SagaEventHandlerInvoker sagaEventHandlerInvoker,
      Serde<SagaEventMessage> sagaEventMessageSerde,
      Serde<SagaResult> sagaResultSerde) {
    return (kStream) ->
        kStream
            .groupBy(
                (messageId, sagaEventMessage) -> sagaEventMessage.getSagaId(),
                Grouped.with(null, sagaEventMessageSerde))
            .aggregate(
                SagaResult::new,
                (messageId, sagaEventMessage, sagaResult) -> {
                  val sagaId = sagaEventMessage.getSagaId();
                  log.debug(
                      "saga event message received. [id={}, sagaId={}, eventType={}]",
                      messageId,
                      sagaId,
                      sagaEventMessage.getPayloadType());
                  sagaResult.setSagaId(sagaId);
                  sagaEventHandlerInvoker.invoke(sagaResult, sagaEventMessage).block();
                  return sagaResult;
                },
                Materialized.<UUID, SagaResult, KeyValueStore<Bytes, byte[]>>as(
                        SagaResult.class.getSimpleName())
                    .withKeySerde(Serdes.UUID())
                    .withValueSerde(sagaResultSerde));
  }
}
