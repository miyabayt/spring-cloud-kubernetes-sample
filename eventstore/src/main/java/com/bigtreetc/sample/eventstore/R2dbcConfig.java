package com.bigtreetc.sample.eventstore;

import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  @Autowired ConnectionFactory connectionFactory;

  @Override
  public ConnectionFactory connectionFactory() {
    return connectionFactory;
  }

  @WritingConverter
  public static class UUIDToStringConverter implements Converter<UUID, String> {
    @Override
    public String convert(UUID source) {
      return source.toString();
    }
  }

  @ReadingConverter
  public static class StringToUUIDConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(String source) {
      return UUID.fromString(source);
    }
  }

  @Override
  protected List<Object> getCustomConverters() {
    return List.of(new UUIDToStringConverter(), new StringToUUIDConverter());
  }

  @Bean
  public ReactiveAuditorAware<String> auditorAware() {
    // TODO: Spring Security and OAuth2
    //    return () -> ReactiveSecurityContextHolder.getContext()
    //            .map(SecurityContext::getAuthentication)
    //            .filter(Authentication::isAuthenticated)
    //            .map(Authentication::getPrincipal)
    //            .map(User.class::cast)
    //            .map(User::getUsername);
    return () -> Mono.just("TODO");
  }
}
