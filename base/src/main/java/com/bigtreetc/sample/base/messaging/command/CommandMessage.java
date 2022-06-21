package com.bigtreetc.sample.base.messaging.command;

import com.bigtreetc.sample.base.messaging.Message;
import java.io.Serial;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class CommandMessage implements Message {

  @Serial private static final long serialVersionUID = 943179339676689340L;

  private UUID id;

  private String payload;

  private String payloadType;

  private String metadata;

  private UUID sourceAggregateId;
}
