package com.bigtreetc.sample.base.messaging.command;

import reactor.core.publisher.Mono;

public interface CommandBus {

  Mono<Boolean> send(CommandMessage message);
}
