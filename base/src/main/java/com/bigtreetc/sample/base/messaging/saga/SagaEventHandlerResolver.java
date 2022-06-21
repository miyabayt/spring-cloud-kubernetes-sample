package com.bigtreetc.sample.base.messaging.saga;

public interface SagaEventHandlerResolver {

  boolean canHandle(String eventType);

  SagaEventHandler resolve(String eventType);
}
