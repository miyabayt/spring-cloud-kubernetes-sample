package com.bigtreetc.sample.base.commandhandling;

import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.command.Command;
import com.bigtreetc.sample.base.messaging.command.CommandMessage;
import com.bigtreetc.sample.base.utils.ClassUtils;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultCommandHandlerInvoker implements CommandHandlerInvoker {

  @NonNull final CommandHandlerResolver commandHandlerResolver;

  @Override
  public Mono<Void> invoke(CommandMessage commandMessage) {
    val id = commandMessage.getId();
    val commandType = commandMessage.getPayloadType();
    val handler = commandHandlerResolver.resolve(commandType);

    if (handler == null) {
      log.info("no handler found: [id={}, commandType={}]", id, commandType);
      return Mono.empty();
    }

    log.info(
        "handle command message: [id={}, commandType={}, handler={}]", id, commandType, handler);
    val payload = commandMessage.getPayload();
    val command = JacksonUtils.readValue(payload, ClassUtils.getClass(Command.class, commandType));
    val metadata = JacksonUtils.readValue(commandMessage.getMetadata(), Metadata.class);

    return handler.handle(command, metadata);
  }
}
