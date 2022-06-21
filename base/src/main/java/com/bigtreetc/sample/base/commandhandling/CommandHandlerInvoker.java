package com.bigtreetc.sample.base.commandhandling;

import com.bigtreetc.sample.base.messaging.command.CommandMessage;
import reactor.core.publisher.Mono;

public interface CommandHandlerInvoker {

  Mono<Void> invoke(CommandMessage message);
}
