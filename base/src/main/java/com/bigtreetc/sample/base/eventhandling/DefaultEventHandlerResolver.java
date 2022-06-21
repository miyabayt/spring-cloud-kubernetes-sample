package com.bigtreetc.sample.base.eventhandling;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;

@Slf4j
public class DefaultEventHandlerResolver implements EventHandlerResolver {

  private final ApplicationContext applicationContext;

  private final Map<String, Class<EventHandler>> eventHandlerMap = new HashMap<>();

  public DefaultEventHandlerResolver(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    val names = this.applicationContext.getBeanNamesForType(EventHandler.class);
    for (val name : names) {
      registerEvent(name);
    }
  }

  @Override
  public EventHandler resolve(String eventType) {
    val handlerClass = eventHandlerMap.get(eventType);
    if (handlerClass == null) return null;
    return this.applicationContext.getBean(handlerClass);
  }

  @SuppressWarnings("unchecked")
  private void registerEvent(String name) {
    val handlerClass = (Class<EventHandler>) this.applicationContext.getType(name);
    if (handlerClass != null) {
      val handler = this.applicationContext.getBean(handlerClass);
      for (val eventType : handler.supports()) {
        if (Event.class.isAssignableFrom(eventType)) {
          eventHandlerMap.put(eventType.getName(), handlerClass);
        }
      }
    }
  }
}
