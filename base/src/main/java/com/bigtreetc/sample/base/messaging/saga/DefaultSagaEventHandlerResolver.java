package com.bigtreetc.sample.base.messaging.saga;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;

@Slf4j
public class DefaultSagaEventHandlerResolver implements SagaEventHandlerResolver {

  private final ApplicationContext applicationContext;

  private final Map<String, Class<SagaEventHandler>> sagaEventHandlerMap = new HashMap<>();

  public DefaultSagaEventHandlerResolver(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    val names = this.applicationContext.getBeanNamesForType(SagaEventHandler.class);
    for (val name : names) {
      registerEvent(name);
    }
  }

  @Override
  public boolean canHandle(String eventType) {
    return sagaEventHandlerMap.get(eventType) != null;
  }

  @Override
  public SagaEventHandler resolve(String eventType) {
    val handlerClass = sagaEventHandlerMap.get(eventType);
    if (handlerClass == null) return null;
    return this.applicationContext.getBean(handlerClass);
  }

  @SuppressWarnings("unchecked")
  private void registerEvent(String name) {
    val handlerClass = (Class<SagaEventHandler>) this.applicationContext.getType(name);
    if (handlerClass != null) {
      val handler = this.applicationContext.getBean(handlerClass);
      for (val eventType : handler.supports()) {
        if (Event.class.isAssignableFrom(eventType)) {
          sagaEventHandlerMap.put(eventType.getName(), handlerClass);
        }
      }
    }
  }
}
