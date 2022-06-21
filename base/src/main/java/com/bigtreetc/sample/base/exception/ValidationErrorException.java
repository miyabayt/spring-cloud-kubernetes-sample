package com.bigtreetc.sample.base.exception;

import java.io.Serial;
import java.util.Optional;
import org.springframework.validation.Errors;

public class ValidationErrorException extends RuntimeException {

  @Serial private static final long serialVersionUID = 3745603124187104651L;

  private Errors errors;

  public ValidationErrorException(Errors errors) {
    super();
    this.errors = errors;
  }

  public Optional<Errors> getErrors() {
    return Optional.of(this.errors);
  }
}
