package com.bigtreetc.sample.base.eventhandling;

public interface EventHandlerResolver {

  EventHandler resolve(String eventType);
}
