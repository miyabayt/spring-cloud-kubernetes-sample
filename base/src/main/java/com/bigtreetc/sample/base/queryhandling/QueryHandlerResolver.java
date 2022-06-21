package com.bigtreetc.sample.base.queryhandling;

public interface QueryHandlerResolver {

  QueryHandler resolve(String queryType);
}
