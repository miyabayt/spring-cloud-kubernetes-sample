package com.bigtreetc.sample.eventstore.domain.service;

import com.bigtreetc.sample.base.messaging.event.EventBus;
import com.bigtreetc.sample.base.messaging.event.EventMessage;
import com.bigtreetc.sample.eventstore.controller.EventDto;
import com.bigtreetc.sample.eventstore.controller.SnapshotDto;
import com.bigtreetc.sample.eventstore.domain.model.R2dbcEventEntity;
import com.bigtreetc.sample.eventstore.domain.model.R2dbcSnapshotEntity;
import com.bigtreetc.sample.eventstore.domain.repository.R2dbcEventRepository;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class R2dbcEventStore {

  @NonNull final R2dbcEventRepository repository;

  @NonNull final EventBus eventBus;

  /**
   * 集約に必要な格納先を作成する。
   *
   * @param aggregateName
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Void> createAggregate(String aggregateName) {
    return this.repository.createTables(aggregateName);
  }

  /**
   * 指定した集約IDに紐づくイベントを取得する。
   *
   * @param aggregateName
   * @param aggregateId
   * @param baseSequence
   * @return
   */
  @Transactional(readOnly = true)
  public Flux<EventDto> readEvents(String aggregateName, UUID aggregateId, int baseSequence) {
    return this.repository
        .findEvents(aggregateName, aggregateId, baseSequence)
        .map(
            entity ->
                EventDto.builder()
                    .aggregateId(entity.getAggregateId())
                    .sequence(entity.getSequence())
                    .eventType(entity.getEventType())
                    .payload(entity.getPayload())
                    .metadata(entity.getMetadata())
                    .build());
  }

  /**
   * イベントを追加する。
   *
   * @param aggregateName
   * @param aggregateId
   * @param events
   * @return
   */
  public Mono<Void> appendEvents(
      String aggregateName, UUID aggregateId, Iterable<EventDto> events) {
    return Flux.fromIterable(events)
        .map(
            event ->
                R2dbcEventEntity.builder()
                    .aggregateId(aggregateId)
                    .sequence(event.getSequence())
                    .eventType(event.getEventType())
                    .payload(event.getPayload())
                    .metadata(event.getMetadata())
                    .build())
        .concatMap(
            entity ->
                this.repository
                    .saveEvent(aggregateName, entity)
                    .flatMap(
                        e -> {
                          val eventMessage =
                              EventMessage.builder()
                                  .id(UUID.randomUUID())
                                  .payload(entity.getPayload())
                                  .payloadType(entity.getEventType())
                                  .metadata(entity.getMetadata())
                                  .sourceAggregateId(entity.getAggregateId())
                                  .build();
                          return eventBus.send(eventMessage);
                        }))
        .then();
  }

  /**
   * 指定した集約IDに紐づくスナップショットを取得する。
   *
   * @param aggregateName
   * @param aggregateId
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<SnapshotDto> getSnapshot(String aggregateName, UUID aggregateId) {
    return this.repository
        .findSnapshot(aggregateName, aggregateId)
        .map(
            entity ->
                SnapshotDto.builder()
                    .aggregateId(entity.getAggregateId())
                    .sequence(entity.getSequence())
                    .payload(entity.getPayload())
                    .metadata(entity.getMetadata())
                    .build());
  }

  /**
   * スナップショットを保存する。
   *
   * @param aggregateName
   * @param snapshots
   * @return
   */
  public Mono<Void> storeSnapshots(String aggregateName, Iterable<SnapshotDto> snapshots) {
    return Flux.fromIterable(snapshots)
        .map(
            s ->
                R2dbcSnapshotEntity.builder()
                    .aggregateId(s.getAggregateId())
                    .sequence(s.getSequence())
                    .payload(s.getPayload())
                    .metadata(s.getMetadata())
                    .build())
        .concatMap(snapshot -> this.repository.saveSnapshot(aggregateName, snapshot))
        .then();
  }
}
