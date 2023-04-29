package com.bigtreetc.sample.bank.exception;

import static com.bigtreetc.sample.base.web.BaseWebConst.VALIDATION_ERROR;

import com.bigtreetc.sample.base.exception.NoDataFoundException;
import com.bigtreetc.sample.base.utils.MessageUtils;
import com.bigtreetc.sample.base.web.dto.ErrorResponseDto;
import com.bigtreetc.sample.base.web.dto.FieldErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AppExceptionHandler extends DefaultErrorAttributes {

  @ExceptionHandler(NoDataFoundException.class)
  public Mono<ResponseEntity<?>> handleException(
      NoDataFoundException e, ServerHttpRequest request) {
    return Mono.just(ResponseEntity.notFound().build());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<?>> handleException(
      ConstraintViolationException e, ServerHttpRequest request) {
    val message = MessageUtils.getMessage(VALIDATION_ERROR);
    val fieldErrors =
        e.getConstraintViolations().stream()
            .map(
                cv ->
                    FieldErrorResponseDto.builder()
                        .fieldName(cv.getPropertyPath().toString())
                        .rejectedValue(cv.getInvalidValue())
                        .errorMessage(cv.getMessage())
                        .build())
            .collect(Collectors.toList());
    val dto =
        ErrorResponseDto.builder()
            .requestId(request.getId())
            .fieldErrors(fieldErrors)
            .message(message)
            .build();
    return Mono.just(ResponseEntity.badRequest().body(dto));
  }

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> map = super.getErrorAttributes(request, options);
    map.put("success", false);
    map.remove("timestamp");
    map.remove("path");
    map.remove("error");
    map.remove("trace");
    return map;
  }
}
