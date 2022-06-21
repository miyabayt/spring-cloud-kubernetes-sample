package com.bigtreetc.sample.base.queryhandling;

import com.bigtreetc.sample.base.messaging.query.Query;
import com.bigtreetc.sample.base.model.Aggregate;
import java.util.List;
import reactor.core.CorePublisher;

public interface QueryHandler {

  <Q extends Query> CorePublisher<? extends Aggregate> handle(Q query);

  List<Class<? extends Query>> supports();
}
