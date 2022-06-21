package com.bigtreetc.sample.base.config;

import com.bigtreetc.sample.base.commandhandling.CommandHandlerInvoker;
import com.bigtreetc.sample.base.commandhandling.CommandHandlerResolver;
import com.bigtreetc.sample.base.commandhandling.DefaultCommandHandlerInvoker;
import com.bigtreetc.sample.base.commandhandling.DefaultCommandHandlerResolver;
import com.bigtreetc.sample.base.eventhandling.DefaultEventHandlerInvoker;
import com.bigtreetc.sample.base.eventhandling.DefaultEventHandlerResolver;
import com.bigtreetc.sample.base.eventhandling.EventHandlerInvoker;
import com.bigtreetc.sample.base.eventhandling.EventHandlerResolver;
import com.bigtreetc.sample.base.eventstore.*;
import com.bigtreetc.sample.base.messaging.QueryableStoreClient;
import com.bigtreetc.sample.base.messaging.command.*;
import com.bigtreetc.sample.base.messaging.event.DefaultEventBus;
import com.bigtreetc.sample.base.messaging.event.EventBus;
import com.bigtreetc.sample.base.messaging.query.DefaultQueryBus;
import com.bigtreetc.sample.base.messaging.query.DefaultQueryGateway;
import com.bigtreetc.sample.base.messaging.query.QueryBus;
import com.bigtreetc.sample.base.messaging.query.QueryGateway;
import com.bigtreetc.sample.base.messaging.saga.*;
import com.bigtreetc.sample.base.queryhandling.DefaultQueryHandlerInvoker;
import com.bigtreetc.sample.base.queryhandling.DefaultQueryHandlerResolver;
import com.bigtreetc.sample.base.queryhandling.QueryHandlerInvoker;
import com.bigtreetc.sample.base.queryhandling.QueryHandlerResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Import({CommonStreamsConfig.class, CommonInteractiveQueryServiceConfig.class})
@EnableConfigurationProperties({EventSourcingProperties.class})
public class CommonEventSourcingConfig {

  @NonNull final EventSourcingProperties eventSourcingProperties;

  @Bean
  public EventStoreClient eventStoreClient(WebClient.Builder webClientBuilder) {
    val baseUrl = eventSourcingProperties.getEventstore().getBaseUrl();
    return new EventStoreClient(webClientBuilder, baseUrl);
  }

  @Bean
  public QueryableStoreClient queryableStoreClient(
      InteractiveQueryService interactiveQueryService, WebClient.Builder webClientBuilder) {
    return new QueryableStoreClient(interactiveQueryService, webClientBuilder);
  }

  @Bean
  public EventSourcingStorage eventSourcingStorage(EventStoreClient eventStoreClient) {
    return new DefaultEventSourcingStorage(eventStoreClient);
  }

  @Bean
  public EventSourcingRepository eventSourcingRepository(
      EventSourcingStorage eventSourcingStorage) {
    return new DefaultEventSourcingRepository(eventSourcingStorage);
  }

  @Bean
  public CommandHandlerResolver commandHandlerResolver(ApplicationContext applicationContext) {
    return new DefaultCommandHandlerResolver(applicationContext);
  }

  @Bean
  public CommandHandlerInvoker commandHandlerInvoker(
      CommandHandlerResolver commandHandlerResolver) {
    return new DefaultCommandHandlerInvoker(commandHandlerResolver);
  }

  @Bean
  public CommandBus commandBus(StreamBridge streamBridge) {
    val topicName = eventSourcingProperties.getMessaging().getCommandTopicName();
    return new DefaultCommandBus(topicName, streamBridge);
  }

  @Bean
  public CommandGateway commandGateway(
      CommandBus commandBus, QueryableStoreClient queryableStoreClient) {
    return new DefaultCommandGateway(commandBus, queryableStoreClient);
  }

  @Bean
  public EventHandlerResolver eventHandlerResolver(ApplicationContext applicationContext) {
    return new DefaultEventHandlerResolver(applicationContext);
  }

  @Bean
  public EventHandlerInvoker eventHandlerInvoker(EventHandlerResolver eventHandlerResolver) {
    return new DefaultEventHandlerInvoker(eventHandlerResolver);
  }

  @Bean
  public EventBus eventBus(StreamBridge streamBridge) {
    val topicName = eventSourcingProperties.getMessaging().getEventTopicName();
    return new DefaultEventBus(topicName, streamBridge);
  }

  @Bean
  public QueryHandlerResolver queryHandlerResolver(ApplicationContext applicationContext) {
    return new DefaultQueryHandlerResolver(applicationContext);
  }

  @Bean
  public QueryHandlerInvoker queryHandlerInvoker(QueryHandlerResolver queryHandlerResolver) {
    return new DefaultQueryHandlerInvoker(queryHandlerResolver);
  }

  @Bean
  public QueryBus queryBus(StreamBridge streamBridge) {
    val topicName = eventSourcingProperties.getMessaging().getQueryTopicName();
    return new DefaultQueryBus(topicName, streamBridge);
  }

  @Bean
  public QueryGateway queryGateway(QueryBus queryBus, QueryableStoreClient queryableStoreClient) {
    return new DefaultQueryGateway(queryBus, queryableStoreClient);
  }

  @Bean
  public SagaEventHandlerResolver sagaEventHandlerResolver(ApplicationContext applicationContext) {
    return new DefaultSagaEventHandlerResolver(applicationContext);
  }

  @Bean
  public SagaEventHandlerInvoker sagaEventHandlerInvoker(
      SagaEventHandlerResolver sagaEventHandlerResolver) {
    return new DefaultSagaEventHandlerInvoker(sagaEventHandlerResolver);
  }

  @Bean
  public SagaEventBus sagaEventBus(StreamBridge streamBridge) {
    val topicName = eventSourcingProperties.getMessaging().getSagaEventTopicName();
    return new DefaultSagaEventBus(topicName, streamBridge);
  }

  @Bean
  public SagaManager sagaManager(
      SagaEventBus sagaEventBus, QueryableStoreClient queryableStoreClient) {
    return new DefaultSagaManager(sagaEventBus, queryableStoreClient);
  }
}
