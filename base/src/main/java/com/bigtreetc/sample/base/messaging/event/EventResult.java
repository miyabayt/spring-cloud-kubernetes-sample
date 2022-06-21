package com.bigtreetc.sample.base.messaging.event;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class EventResult implements Serializable {

  @Serial private static final long serialVersionUID = -350325937362075991L;

  private UUID aggregateId;

  private String payload;

  private String payloadType;

  private String metadata;
}
