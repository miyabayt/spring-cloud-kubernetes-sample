package com.bigtreetc.sample.base.messaging.saga;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class SagaStep {

  private String payload;

  private String eventType;

  private SagaStepStatus status;
}
