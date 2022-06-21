package com.bigtreetc.sample.base.messaging.command;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.io.Serializable;
import java.util.UUID;

public interface Command extends Serializable {

  UUID getAggregateId();

  default CommandMessage toCommandMessage() {
    return toCommandMessage(new Metadata());
  }

  default CommandMessage toCommandMessage(Metadata metadata) {
    return CommandMessage.builder()
        .id(UUID.randomUUID())
        .payload(JacksonUtils.writeValueAsString(this))
        .payloadType(this.getClass().getName())
        .metadata(JacksonUtils.writeValueAsString(metadata))
        .sourceAggregateId(this.getAggregateId())
        .build();
  }
}
