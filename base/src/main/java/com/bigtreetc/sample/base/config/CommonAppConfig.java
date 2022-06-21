package com.bigtreetc.sample.base.config;

import com.bigtreetc.sample.base.utils.MessageUtils;
import com.bigtreetc.sample.base.web.filter.ElapsedMillisLoggingFilter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

public class CommonAppConfig {

  @Bean
  public ElapsedMillisLoggingFilter elapsedMillisLoggingFilter() {
    return new ElapsedMillisLoggingFilter();
  }

  @Bean
  public ForwardedHeaderTransformer forwardedHeaderTransformer() {
    return new ForwardedHeaderTransformer();
  }

  @Bean
  @Primary
  public LocalValidatorFactoryBean beanValidator(MessageSource messageSource) {
    val bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource);
    return bean;
  }

  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor(
      LocalValidatorFactoryBean localValidatorFactoryBean) {
    val bean = new MethodValidationPostProcessor();
    bean.setValidator(localValidatorFactoryBean);
    return bean;
  }

  @Autowired
  public void initUtils(MessageSource messageSource) {
    MessageUtils.init(messageSource);
  }
}
