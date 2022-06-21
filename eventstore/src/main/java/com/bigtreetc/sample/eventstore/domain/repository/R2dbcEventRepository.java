package com.bigtreetc.sample.eventstore.domain.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

import com.bigtreetc.sample.eventstore.domain.model.R2dbcAggregateEntity;
import com.bigtreetc.sample.eventstore.domain.model.R2dbcEventEntity;
import com.bigtreetc.sample.eventstore.domain.model.R2dbcSnapshotEntity;
import com.bigtreetc.sample.eventstore.exception.SequenceNumberMismatchException;
import java.util.Objects;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
@Slf4j
public class R2dbcEventRepository
    implements EventRepository<R2dbcEventEntity, R2dbcSnapshotEntity> {

  @NonNull final DatabaseClient databaseClient;

  @NonNull final R2dbcEntityTemplate template;

  @Override
  public Mono<Void> createTables(String aggregateName) {
    val tableName = aggregateName.toLowerCase();
    val createAggregateTableSql =
        "CREATE TABLE IF NOT EXISTS "
            + tableName
            + "_aggregates( "
            + "  aggregate_id VARCHAR(36) NOT NULL "
            + "  , sequence INT unsigned NOT NULL DEFAULT 1 "
            + "  , PRIMARY KEY (aggregate_id) "
            + ") ENGINE=InnoDB; ";
    val createEventTableSql =
        "CREATE TABLE IF NOT EXISTS "
            + tableName
            + "_events( "
            + "  aggregate_id VARCHAR(36) NOT NULL "
            + "  , sequence INT unsigned NOT NULL DEFAULT 1 "
            + "  , event_type VARCHAR(200) NOT NULL "
            + "  , payload TEXT NOT NULL "
            + "  , metadata TEXT NOT NULL "
            + "  , created_at DATETIME NOT NULL "
            + "  , created_by VARCHAR(50) NOT NULL "
            + "  , UNIQUE KEY uidx_"
            + tableName
            + "_events (aggregate_id, sequence) "
            + ") ENGINE=InnoDB PARTITION BY LINEAR KEY (aggregate_id) PARTITIONS 16; ";
    val createSnapshotTableSql =
        "CREATE TABLE IF NOT EXISTS "
            + tableName
            + "_snapshots( "
            + "  aggregate_id VARCHAR(36) NOT NULL "
            + "  , sequence INT unsigned NOT NULL "
            + "  , payload TEXT NOT NULL "
            + "  , metadata TEXT NOT NULL "
            + "  , created_at DATETIME NOT NULL "
            + "  , updated_at DATETIME NOT NULL "
            + "  , PRIMARY KEY (aggregate_id) "
            + ") ENGINE=InnoDB; ";
    return this.databaseClient
        .sql(createAggregateTableSql + createEventTableSql + createSnapshotTableSql)
        .then();
  }

  @Override
  public Flux<R2dbcEventEntity> findEvents(
      String aggregateName, UUID aggregateId, long baseSequence) {
    val eventTableName = getTableName("%s_events", aggregateName);
    return this.template
        .select(R2dbcEventEntity.class)
        .from(eventTableName)
        .matching(
            query(
                    where("aggregate_id")
                        .is(aggregateId.toString())
                        .and("sequence")
                        .greaterThanOrEquals(baseSequence))
                .sort(Sort.by("sequence").ascending()))
        .all();
  }

  @Override
  public Mono<R2dbcEventEntity> saveEvent(String aggregateName, R2dbcEventEntity event) {
    val eventTableName = getTableName("%s_events", aggregateName);
    val aggregateTableName = getTableName("%s_aggregates", aggregateName);
    val aggregateId = event.getAggregateId();
    return findAggregate(aggregateTableName, aggregateId)
        .flatMap(
            e ->
                checkSequence(e.getSequence() + 1, event.getSequence())
                    .then(updateAggregate(aggregateTableName, event))
                    .then(insertEvent(eventTableName, event)))
        .switchIfEmpty(
            checkSequence(1, event.getSequence())
                .then(insertAggregate(aggregateTableName, event))
                .then(insertEvent(eventTableName, event)));
  }

  @Override
  public Mono<R2dbcSnapshotEntity> findSnapshot(String aggregateName, UUID aggregateId) {
    val snapshotTableName = getTableName("%s_snapshots", aggregateName);
    return this.template
        .select(R2dbcSnapshotEntity.class)
        .from(snapshotTableName)
        .matching(query(where("aggregate_id").is(aggregateId.toString())))
        .one();
  }

  @Override
  public Mono<R2dbcSnapshotEntity> saveSnapshot(
      String aggregateName, R2dbcSnapshotEntity snapshot) {
    val snapshotTableName = getTableName("%s_snapshots", aggregateName);
    val aggregateId = snapshot.getAggregateId();
    return findSnapshot(aggregateName, aggregateId)
        .flatMap(s -> updateSnapshot(snapshotTableName, snapshot))
        .switchIfEmpty(insertSnapshot(snapshotTableName, snapshot));
  }

  private Mono<?> checkSequence(int expected, int actual) {
    if (!Objects.equals(expected, actual)) {
      return Mono.error(new SequenceNumberMismatchException(expected, actual));
    }
    return Mono.empty();
  }

  private Mono<R2dbcAggregateEntity> findAggregate(String aggregateTableName, UUID aggregateId) {
    return this.template
        .select(R2dbcAggregateEntity.class)
        .from(aggregateTableName)
        .matching(query(where("aggregate_id").is(aggregateId.toString())))
        .one();
  }

  private Mono<R2dbcAggregateEntity> insertAggregate(String tableName, R2dbcEventEntity event) {
    val aggregateId = event.getAggregateId().toString();
    val sequence = event.getSequence();
    val eventType = event.getEventType();
    val aggregateEntity =
        R2dbcAggregateEntity.builder().aggregateId(event.getAggregateId()).sequence(1).build();
    return this.template
        .insert(R2dbcAggregateEntity.class)
        .into(tableName)
        .using(aggregateEntity)
        .doOnSuccess(
            done ->
                log.info(
                    "aggregate inserted: [aggregateId={}, sequence={}, eventType={}]",
                    aggregateId,
                    sequence,
                    eventType));
  }

  private Mono<R2dbcEventEntity> updateAggregate(String tableName, R2dbcEventEntity event) {
    val aggregateId = event.getAggregateId().toString();
    val sequence = event.getSequence();
    return this.template
        .update(R2dbcAggregateEntity.class)
        .inTable(tableName)
        .matching(query(where("aggregate_id").is(aggregateId)))
        .apply(update("sequence", sequence))
        .flatMap(
            updated ->
                updated == 0
                    ? Mono.error(new SequenceNumberMismatchException())
                    : Mono.just(updated))
        .thenReturn(event)
        .doOnSuccess(
            done ->
                log.info(
                    "aggregate updated: [aggregateId={}, sequence={}]", aggregateId, sequence));
  }

  private Mono<R2dbcEventEntity> insertEvent(String tableName, R2dbcEventEntity event) {
    val aggregateId = event.getAggregateId();
    val sequence = event.getSequence();
    val eventType = event.getEventType();
    val newEvent =
        R2dbcEventEntity.builder()
            .aggregateId(aggregateId)
            .sequence(sequence)
            .eventType(eventType)
            .payload(event.getPayload())
            .metadata(event.getMetadata())
            .build();
    return this.template
        .insert(R2dbcEventEntity.class)
        .into(tableName)
        .using(newEvent)
        .doOnSuccess(
            done ->
                log.info(
                    "event inserted: [aggregateId={}, sequence={}, eventType={}]",
                    aggregateId,
                    sequence,
                    eventType));
  }

  private Mono<R2dbcSnapshotEntity> insertSnapshot(String tableName, R2dbcSnapshotEntity snapshot) {
    val aggregateId = snapshot.getAggregateId();
    val sequence = snapshot.getSequence();
    return this.template
        .insert(R2dbcSnapshotEntity.class)
        .into(tableName)
        .using(snapshot)
        .doOnSuccess(
            done ->
                log.info(
                    "snapshot inserted: [aggregateId={}, sequence={}]", aggregateId, sequence));
  }

  private Mono<R2dbcSnapshotEntity> updateSnapshot(String tableName, R2dbcSnapshotEntity snapshot) {
    val aggregateId = snapshot.getAggregateId().toString();
    val sequence = snapshot.getSequence();
    val payload = snapshot.getPayload();
    val metadata = snapshot.getMetadata();
    return this.template
        .update(R2dbcSnapshotEntity.class)
        .inTable(tableName)
        .matching(query(where("aggregate_id").is(aggregateId).and("sequence").lessThan(sequence)))
        .apply(update("sequence", sequence).set("payload", payload).set("metadata", metadata))
        .flatMap(
            updated ->
                updated == 0
                    ? Mono.error(new SequenceNumberMismatchException())
                    : Mono.just(updated))
        .thenReturn(snapshot)
        .doOnSuccess(
            done ->
                log.info("snapshot updated: [aggregateId={}, sequence={}]", aggregateId, sequence));
  }

  private String getTableName(String format, String suffix) {
    return String.format(format, suffix.toLowerCase());
  }
}
