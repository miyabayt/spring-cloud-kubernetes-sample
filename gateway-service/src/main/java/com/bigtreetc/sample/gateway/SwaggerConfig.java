package com.bigtreetc.sample.gateway;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SwaggerConfig {

  @Bean
  public CommandLineRunner openApiGroups(
      RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiConfigParameters) {
    return args ->
        Objects.requireNonNull(locator.getRouteDefinitions().collectList().block()).stream()
            .map(RouteDefinition::getId)
            .map(id -> id.replace("ReactiveCompositeDiscoveryClient_", ""))
            .filter(id -> id.matches(".*-service") && !id.equals("gateway-service"))
            .forEach(swaggerUiConfigParameters::addGroup);
  }
}
