package com.bigtreetc.sample.base.messaging.saga;

import com.bigtreetc.sample.base.messaging.Message;
import java.io.Serial;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class SagaEventMessage implements Message {

  @Serial private static final long serialVersionUID = 9201242991685181510L;

  private UUID id;

  private UUID sagaId;

  private String payload;

  private String payloadType;

  private String metadata;
}
