package com.bigtreetc.sample.base.model;

import com.bigtreetc.sample.base.eventstore.EventMessageEntry;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.utils.ClassUtils;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.annotation.Transient;

@NoArgsConstructor
@SuperBuilder
@Slf4j
public abstract class AggregateRoot implements Aggregate {

  @JsonIgnore @Transient private final List<Event> uncommittedEvents = new ArrayList<>();

  @JsonIgnore @Transient private Integer sequence;

  @Override
  public void load(List<EventMessageEntry> eventMessageEntries) {
    sequence = 1;
    for (val eventMessageEntry : eventMessageEntries) {
      val typeName = eventMessageEntry.getEventType();
      val type = ClassUtils.getClass(Event.class, typeName);
      val event = JacksonUtils.readValue(eventMessageEntry.getPayload(), type);
      val metadata = JacksonUtils.readValue(eventMessageEntry.getMetadata(), Metadata.class);
      applyChange(event, metadata, false);
      sequence++;
    }
  }

  @Override
  public void apply(Event event, Metadata metadata) {
    applyChange(event, metadata, true);
  }

  @Override
  public boolean hasUncommittedEvents() {
    return !uncommittedEvents.isEmpty();
  }

  @Override
  public List<Event> getUncommittedEvents() {
    return Collections.unmodifiableList(uncommittedEvents);
  }

  @Override
  public void flushUncommittedEvents() {
    uncommittedEvents.clear();
  }

  protected void applyChange(Event event, Metadata metadata, boolean isNew) {
    handle(event, metadata);
    if (isNew) {
      uncommittedEvents.add(event);
    }
    if (sequence == null) {
      sequence = 1;
    }
    log.debug(
        "event applied. [aggregate={}, event={}, seq={}]",
        getClass().getSimpleName(),
        event.getClass().getSimpleName(),
        sequence);
  }
}
