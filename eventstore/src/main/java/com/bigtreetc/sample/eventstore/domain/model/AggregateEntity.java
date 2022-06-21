package com.bigtreetc.sample.eventstore.domain.model;

import java.util.UUID;

public interface AggregateEntity {

  UUID getAggregateId();

  Integer getSequence();
}
