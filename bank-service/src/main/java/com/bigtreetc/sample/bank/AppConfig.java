package com.bigtreetc.sample.bank;

import com.bigtreetc.sample.base.config.CommonAppConfig;
import com.bigtreetc.sample.base.config.CommonEventSourcingConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

@Configuration
@Import({CommonAppConfig.class, CommonEventSourcingConfig.class})
@EnableConfigurationProperties(FlywayProperties.class)
@OpenAPIDefinition(info = @Info(title = "Bank Service", version = "0.0.1"))
@Slf4j
public class AppConfig {

  @Bean(initMethod = "migrate")
  public Flyway flyway(FlywayProperties flywayProperties) {
    return new Flyway(
        Flyway.configure()
            .baselineOnMigrate(flywayProperties.isBaselineOnMigrate())
            .placeholderReplacement(flywayProperties.isPlaceholderReplacement())
            .schemas(StringUtils.toStringArray(flywayProperties.getSchemas()))
            .dataSource(
                flywayProperties.getUrl(),
                flywayProperties.getUser(),
                flywayProperties.getPassword()));
  }
}
