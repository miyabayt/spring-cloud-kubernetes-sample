package com.bigtreetc.sample.base.queryhandling;

import com.bigtreetc.sample.base.messaging.query.QueryMessage;
import com.bigtreetc.sample.base.model.Aggregate;
import reactor.core.CorePublisher;

public interface QueryHandlerInvoker {

  CorePublisher<? extends Aggregate> invoke(QueryMessage message);
}
