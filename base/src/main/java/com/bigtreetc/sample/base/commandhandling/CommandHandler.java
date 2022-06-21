package com.bigtreetc.sample.base.commandhandling;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.command.Command;
import java.util.List;
import reactor.core.publisher.Mono;

public interface CommandHandler {

  List<Class<? extends Command>> supports();

  <C extends Command> Mono<Void> handle(C command, Metadata metadata);
}
