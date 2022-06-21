package com.bigtreetc.sample.eventstore.domain.model;

import java.time.LocalDateTime;

public interface EventEntity {

  Integer getSequence();

  String getEventType();

  String getPayload();

  String getMetadata();

  LocalDateTime getCreatedAt();
}
