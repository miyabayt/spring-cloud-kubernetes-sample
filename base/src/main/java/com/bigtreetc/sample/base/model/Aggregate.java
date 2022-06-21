package com.bigtreetc.sample.base.model;

import com.bigtreetc.sample.base.eventstore.EventMessageEntry;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.UUID;

public interface Aggregate {

  @JsonIgnore
  UUID getAggregateId();

  boolean hasUncommittedEvents();

  List<Event> getUncommittedEvents();

  void flushUncommittedEvents();

  void load(List<EventMessageEntry> eventMessageEntries);

  void apply(Event event, Metadata metadata);

  <E extends Event> void handle(E event, Metadata metadata);
}
