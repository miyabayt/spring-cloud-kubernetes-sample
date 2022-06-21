package com.bigtreetc.sample.hello;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.val;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import reactor.core.publisher.Mono;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Hello Service", version = "0.0.1"))
public class ApiConfig {

  @Bean
  public ForwardedHeaderTransformer forwardedHeaderTransformer() {
    return new ForwardedHeaderTransformer();
  }

  @Bean
  @RouterOperations({
    @RouterOperation(
        path = "/hello",
        produces = {MediaType.APPLICATION_JSON_VALUE},
        operation =
            @Operation(
                operationId = "sayHello",
                responses = {@ApiResponse(responseCode = "200")}))
  })
  public RouterFunction<ServerResponse> helloRouter() {
    return route().GET("/hello", this::helloHandler).build();
  }

  private Mono<ServerResponse> helloHandler(ServerRequest request) {
    val name = request.queryParam("name").orElse("World");
    val message = String.format("Hello, %s!", name);
    return ok().body(fromValue(message));
  }
}
