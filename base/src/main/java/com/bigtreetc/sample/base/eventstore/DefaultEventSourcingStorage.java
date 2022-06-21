package com.bigtreetc.sample.base.eventstore;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.util.Collections;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultEventSourcingStorage implements EventSourcingStorage {

  @NonNull final EventStoreClient eventStoreClient;

  @Override
  public Flux<EventMessageEntry> find(String aggregateName, UUID aggregateId) {
    return eventStoreClient.getEvents(aggregateName, aggregateId);
  }

  @Override
  public Mono<Void> save(String aggregateName, Event event, Metadata metadata) {
    val aggregateId = event.getAggregateId();
    return eventStoreClient
        .getEvents(aggregateName, aggregateId)
        .map(EventMessageEntry::getSequence)
        .collectList()
        .flatMap(
            seqList -> {
              if (seqList.isEmpty()) {
                return appendEvent(aggregateName, event, metadata, 1);
              } else {
                val currentSeq = Collections.max(seqList);
                return appendEvent(aggregateName, event, metadata, currentSeq + 1);
              }
            });
  }

  private Mono<Void> appendEvent(
      String aggregateName, Event event, Metadata metadata, int sequence) {
    val aggregateId = event.getAggregateId();
    val dto =
        EventMessageEntry.builder()
            .aggregateId(aggregateId)
            .sequence(sequence)
            .eventType(event.getClass().getName())
            .payload(JacksonUtils.writeValueAsString(event))
            .metadata(JacksonUtils.writeValueAsString(metadata))
            .build();
    return eventStoreClient.appendEvent(aggregateName, aggregateId, dto);
  }
}
