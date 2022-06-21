package com.bigtreetc.sample.base.messaging.query;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class QueryResult implements Serializable {

  @Serial private static final long serialVersionUID = 988641071585140417L;

  private String payload;

  private String payloadType;
}
