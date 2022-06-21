package com.bigtreetc.sample.base.commandhandling;

import com.bigtreetc.sample.base.messaging.command.Command;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;

@Slf4j
public class DefaultCommandHandlerResolver implements CommandHandlerResolver {

  private final ApplicationContext applicationContext;

  private final Map<String, Class<CommandHandler>> commandHandlerMap = new HashMap<>();

  public DefaultCommandHandlerResolver(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    val names = this.applicationContext.getBeanNamesForType(CommandHandler.class);
    for (val name : names) {
      registerCommand(name);
    }
  }

  @Override
  public CommandHandler resolve(String commandType) {
    val handlerClass = commandHandlerMap.get(commandType);
    if (handlerClass == null) return null;
    return this.applicationContext.getBean(handlerClass);
  }

  @SuppressWarnings("unchecked")
  private void registerCommand(String name) {
    val handlerClass = (Class<CommandHandler>) this.applicationContext.getType(name);
    if (handlerClass != null) {
      val handler = this.applicationContext.getBean(handlerClass);
      for (val commandType : handler.supports()) {
        if (Command.class.isAssignableFrom(commandType)) {
          commandHandlerMap.put(commandType.getName(), handlerClass);
        }
      }
    }
  }
}
