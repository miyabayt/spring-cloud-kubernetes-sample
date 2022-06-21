package com.bigtreetc.sample.base.messaging.command;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class CommandResult implements Serializable {

  @Serial private static final long serialVersionUID = 988641071585140417L;

  private UUID aggregateId;

  private String payload;

  private String payloadType;

  private String metadata;
}
