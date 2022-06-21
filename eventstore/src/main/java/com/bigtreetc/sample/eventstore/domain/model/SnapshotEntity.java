package com.bigtreetc.sample.eventstore.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SnapshotEntity {

  UUID getAggregateId();

  Integer getSequence();

  LocalDateTime getCreatedAt();

  LocalDateTime getUpdatedAt();
}
