package com.bigtreetc.sample.eventstore.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventDto {

  private UUID aggregateId;

  @Min(1)
  private Integer sequence;

  @NotEmpty private String eventType;

  @NotEmpty private String payload;

  private String metadata;
}
