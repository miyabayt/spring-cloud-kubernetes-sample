package com.bigtreetc.sample.eventstore.controller;

import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SnapshotDto {

  @NotEmpty private UUID aggregateId;

  @Min(1)
  private Integer sequence;

  @NotEmpty private String payload;

  private String metadata;
}
