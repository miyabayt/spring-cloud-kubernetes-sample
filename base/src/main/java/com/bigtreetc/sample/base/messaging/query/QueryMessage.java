package com.bigtreetc.sample.base.messaging.query;

import com.bigtreetc.sample.base.messaging.Message;
import java.io.Serial;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class QueryMessage implements Message {

  @Serial private static final long serialVersionUID = -440111895307921644L;

  private UUID id;

  private String payload;

  private String payloadType;

  private String metadata;
}
