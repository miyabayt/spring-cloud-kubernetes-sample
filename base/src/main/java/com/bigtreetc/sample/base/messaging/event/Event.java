package com.bigtreetc.sample.base.messaging.event;

import java.io.Serializable;
import java.util.UUID;

public interface Event extends Serializable {

  UUID getAggregateId();
}
