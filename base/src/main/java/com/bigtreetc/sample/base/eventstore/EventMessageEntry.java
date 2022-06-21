package com.bigtreetc.sample.base.eventstore;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class EventMessageEntry {

  private UUID aggregateId;

  private Integer sequence;

  private String eventType;

  private String payload;

  private String metadata;
}
