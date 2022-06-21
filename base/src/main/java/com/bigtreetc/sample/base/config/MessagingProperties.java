package com.bigtreetc.sample.base.config;

import lombok.Data;

@Data
public class MessagingProperties {

  private String commandTopicName;

  private String queryTopicName;

  private String eventTopicName;

  private String sagaEventTopicName;
}
