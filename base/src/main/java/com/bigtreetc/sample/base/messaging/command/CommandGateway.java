package com.bigtreetc.sample.base.messaging.command;

import com.bigtreetc.sample.base.messaging.Metadata;
import reactor.core.publisher.Mono;

public interface CommandGateway {

  Mono<Void> send(Command command);

  Mono<Void> send(Command command, CommandCallback callback);

  Mono<Void> send(Command command, Metadata metadata);

  Mono<Void> send(Command command, Metadata metadata, CommandCallback callback);

  Mono<CommandResult> sendAndWait(Command command);

  Mono<CommandResult> sendAndWait(Command command, Metadata metadata);
}
