package com.bigtreetc.sample.base.web.filter;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class ElapsedMillisLoggingFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    val beforeNanoSec = System.nanoTime();
    return chain
        .filter(exchange)
        .doFinally(
            done -> {
              val requestPath = exchange.getRequest().getPath().value();
              val requestMethod = exchange.getRequest().getMethod();
              val responseStatus = exchange.getResponse().getStatusCode();
              val elapsedNanoSec = System.nanoTime() - beforeNanoSec;
              val elapsedMilliSec = NANOSECONDS.toMillis(elapsedNanoSec);
              log.info(
                  "path={}, method={}, status={}, Elapsed={}ms.",
                  requestPath,
                  requestMethod,
                  responseStatus,
                  elapsedMilliSec);
            });
  }
}
