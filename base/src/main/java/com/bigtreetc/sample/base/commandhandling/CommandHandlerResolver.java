package com.bigtreetc.sample.base.commandhandling;

public interface CommandHandlerResolver {

  CommandHandler resolve(String commandType);
}
