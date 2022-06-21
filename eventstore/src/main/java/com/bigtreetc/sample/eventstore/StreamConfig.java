package com.bigtreetc.sample.eventstore;

import com.bigtreetc.sample.base.messaging.command.*;
import com.bigtreetc.sample.base.messaging.event.DefaultEventBus;
import com.bigtreetc.sample.base.messaging.event.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StreamConfig {

  @Value("${eventsourcing.messaging.eventTopicName}")
  private String eventTopicName;

  @Bean
  public EventBus eventBus(StreamBridge streamBridge) {
    return new DefaultEventBus(eventTopicName, streamBridge);
  }
}
