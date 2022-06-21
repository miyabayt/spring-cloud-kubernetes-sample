package com.bigtreetc.sample.base.queryhandling;

import com.bigtreetc.sample.base.messaging.query.Query;
import com.bigtreetc.sample.base.messaging.query.QueryMessage;
import com.bigtreetc.sample.base.model.Aggregate;
import com.bigtreetc.sample.base.utils.ClassUtils;
import com.bigtreetc.sample.base.utils.JacksonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class DefaultQueryHandlerInvoker implements QueryHandlerInvoker {

  @NonNull final QueryHandlerResolver queryHandlerResolver;

  @Override
  public CorePublisher<? extends Aggregate> invoke(QueryMessage queryMessage) {
    val id = queryMessage.getId();
    val queryType = queryMessage.getPayloadType();
    val handler = queryHandlerResolver.resolve(queryType);

    if (handler == null) {
      log.info("no handler found: [id={}, queryType={}]", id, queryType);
      return Mono.empty();
    }

    log.info("handle query message: [id={}, queryType={}, handler={}]", id, queryType, handler);
    val payload = queryMessage.getPayload();
    val query = JacksonUtils.readValue(payload, ClassUtils.getClass(Query.class, queryType));

    return handler.handle(query);
  }
}
