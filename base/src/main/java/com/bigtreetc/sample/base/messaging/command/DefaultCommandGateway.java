package com.bigtreetc.sample.base.messaging.command;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.QueryableStoreClient;
import java.time.Duration;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

@RequiredArgsConstructor
@Slf4j
public class DefaultCommandGateway implements CommandGateway {

  @NonNull final CommandBus commandBus;

  @NonNull final QueryableStoreClient queryableStoreClient;

  @Override
  public Mono<Void> send(Command command) {
    return send(command, new Metadata());
  }

  @Override
  public Mono<Void> send(Command command, CommandCallback callback) {
    return send(command, new Metadata(), callback);
  }

  @Override
  public Mono<Void> send(Command command, Metadata metadata) {
    return send(
        command,
        new Metadata(),
        result ->
            log.info("command executed successfully. [aggregateID={}]", result.getAggregateId()));
  }

  @Override
  public Mono<Void> send(Command command, Metadata metadata, CommandCallback callback) {
    return Mono.defer(
            () -> {
              val commandMessage = command.toCommandMessage(metadata);
              return commandBus.send(commandMessage).thenReturn(commandMessage);
            })
        .flatMap(
            c ->
                getCommandResult(c.getId())
                    .repeatWhenEmpty(
                        Repeat.onlyIf(repeatContext -> true)
                            .exponentialBackoff(Duration.ofMillis(25), Duration.ofMillis(500))
                            .timeout(Duration.ofSeconds(10))))
        .doOnNext(result -> log.info("command sent. {}", command))
        .then();
  }

  @Override
  public Mono<CommandResult> sendAndWait(Command command) {
    return sendAndWait(command, new Metadata());
  }

  @Override
  public Mono<CommandResult> sendAndWait(Command command, Metadata metadata) {
    return Mono.defer(
            () -> {
              val commandMessage = command.toCommandMessage(metadata);
              return commandBus.send(commandMessage).thenReturn(commandMessage);
            })
        .flatMap(
            c ->
                getCommandResult(c.getId())
                    .repeatWhenEmpty(
                        Repeat.onlyIf(repeatContext -> true)
                            .exponentialBackoff(Duration.ofMillis(25), Duration.ofMillis(500))
                            .timeout(Duration.ofSeconds(10))))
        .doOnSuccess(result -> log.info("done. {}", result));
  }

  private Mono<CommandResult> getCommandResult(UUID messageId) {
    return Mono.defer(
        () -> {
          try {
            log.debug("get command result. [messageId={}]", messageId);
            return queryableStoreClient.getStoredValue(CommandResult.class, messageId);
          } catch (InvalidStateStoreException e) {
            // ignore
          } catch (Exception e) {
            log.warn("failed to get store. [cause={}]", e.getMessage());
          }
          return Mono.empty();
        });
  }
}
