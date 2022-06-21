package com.bigtreetc.sample.base.messaging.query;

import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.io.Serializable;
import java.util.UUID;

public interface Query extends Serializable {

  default QueryMessage toQueryMessage() {
    return QueryMessage.builder()
        .id(UUID.randomUUID())
        .payload(JacksonUtils.writeValueAsString(this))
        .payloadType(this.getClass().getName())
        .build();
  }
}
