package com.bigtreetc.sample.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("eventsourcing")
public class EventSourcingProperties {

  private EventStoreProperties eventstore;

  private MessagingProperties messaging;
}
