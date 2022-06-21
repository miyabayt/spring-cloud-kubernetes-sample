package com.bigtreetc.sample.eventstore.controller;

import com.bigtreetc.sample.eventstore.domain.service.R2dbcEventStore;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EventStoreController {

  private static final String AGGREGATE_NAME_PATTERN = "[a-zA-Z]+";

  @NonNull final R2dbcEventStore eventStore;

  /**
   * 集約に必要な格納先を作成する。
   *
   * @param aggregateName
   * @return
   */
  @PostMapping("/aggregates/{aggregateName}")
  public Mono<ResponseEntity<Void>> createAggregate(
      @PathVariable @Pattern(regexp = AGGREGATE_NAME_PATTERN) String aggregateName) {
    return eventStore.createAggregate(aggregateName).thenReturn(ResponseEntity.ok().build());
  }

  /**
   * 指定した集約に紐づくイベントを取得する。
   *
   * @param aggregateName
   * @param aggregateId
   * @param baseSequence
   * @return
   */
  @GetMapping("/events/{aggregateName}/{aggregateId}")
  public Mono<ResponseEntity<List<EventDto>>> getEvents(
      @PathVariable @Pattern(regexp = AGGREGATE_NAME_PATTERN) String aggregateName,
      @PathVariable UUID aggregateId,
      @RequestParam(defaultValue = "1") int baseSequence) {
    return eventStore
        .readEvents(aggregateName, aggregateId, baseSequence)
        .collectList()
        .map(events -> ResponseEntity.ok().body(events))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * 指定した集約に紐づくイベントを保存する。
   *
   * @param aggregateName
   * @param aggregateId
   * @param events
   * @return
   */
  @PostMapping("/events/{aggregateName}/{aggregateId}")
  public Mono<Void> appendEvents(
      @PathVariable @Pattern(regexp = AGGREGATE_NAME_PATTERN) String aggregateName,
      @PathVariable UUID aggregateId,
      @RequestBody List<EventDto> events) {
    return eventStore.appendEvents(aggregateName, aggregateId, events);
  }

  /**
   * 指定した集約のスナップショットを取得する。
   *
   * @param aggregateName
   * @param aggregateId
   * @return
   */
  @GetMapping("/snapshots/{aggregateName}/{aggregateId}")
  public Mono<ResponseEntity<SnapshotDto>> getSnapshot(
      @PathVariable @Pattern(regexp = AGGREGATE_NAME_PATTERN) String aggregateName,
      @PathVariable UUID aggregateId) {
    return eventStore
        .getSnapshot(aggregateName, aggregateId)
        .map(events -> ResponseEntity.ok().body(events))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * 指定した集約のスナップショットを保存する。
   *
   * @param aggregateName
   * @param snapshots
   * @return
   */
  @PostMapping("/snapshots/{aggregateName}")
  public Mono<Void> createSnapshot(
      @PathVariable @Pattern(regexp = AGGREGATE_NAME_PATTERN) String aggregateName,
      @RequestBody List<SnapshotDto> snapshots) {
    return eventStore.storeSnapshots(aggregateName, snapshots);
  }
}
