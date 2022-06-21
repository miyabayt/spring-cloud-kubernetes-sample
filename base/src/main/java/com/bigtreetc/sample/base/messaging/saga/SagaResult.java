package com.bigtreetc.sample.base.messaging.saga;

import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import lombok.val;

@Data
public class SagaResult implements Serializable {

  @Serial private static final long serialVersionUID = 988641071585140417L;

  private UUID sagaId;

  private List<SagaStep> sagaSteps;

  private SagaStatus sagaStatus;

  public SagaResult() {
    this.sagaSteps = new ArrayList<>();
    this.sagaStatus = SagaStatus.STARTED;
  }

  public void addSagaStep(Event event, SagaStepStatus status) {
    val payload = JacksonUtils.writeValueAsString(event);
    val eventType = event.getClass().getName();
    sagaSteps.add(SagaStep.builder().payload(payload).eventType(eventType).status(status).build());
  }

  public void setSagaStepStatus(Event event, SagaStepStatus status) {
    val eventType = event.getClass().getName();
    sagaSteps.stream()
        .filter(step -> Objects.equals(step.getEventType(), eventType))
        .findFirst()
        .ifPresent(step -> step.setStatus(status));
  }

  public void setSagaStatus(SagaStatus status) {
    this.sagaStatus = status;
  }
}
