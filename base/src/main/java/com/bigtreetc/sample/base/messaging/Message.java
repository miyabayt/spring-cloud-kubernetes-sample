package com.bigtreetc.sample.base.messaging;

import java.io.Serializable;
import java.util.UUID;

public interface Message extends Serializable {

  UUID getId();

  String getPayload();

  String getPayloadType();

  String getMetadata();
}
