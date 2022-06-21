package com.bigtreetc.sample.base.queryhandling;

import com.bigtreetc.sample.base.messaging.query.Query;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.ApplicationContext;

@Slf4j
public class DefaultQueryHandlerResolver implements QueryHandlerResolver {

  private final ApplicationContext applicationContext;

  private final Map<String, Class<QueryHandler>> queryHandlerMap = new HashMap<>();

  public DefaultQueryHandlerResolver(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    val names = this.applicationContext.getBeanNamesForType(QueryHandler.class);
    for (val name : names) {
      registerQuery(name);
    }
  }

  @Override
  public QueryHandler resolve(String queryType) {
    val handlerClass = queryHandlerMap.get(queryType);
    if (handlerClass == null) return null;
    return this.applicationContext.getBean(handlerClass);
  }

  @SuppressWarnings("unchecked")
  private void registerQuery(String name) {
    val handlerClass = (Class<QueryHandler>) this.applicationContext.getType(name);
    if (handlerClass != null) {
      val handler = this.applicationContext.getBean(handlerClass);
      for (val queryType : handler.supports()) {
        if (Query.class.isAssignableFrom(queryType)) {
          queryHandlerMap.put(queryType.getName(), handlerClass);
        }
      }
    }
  }
}
