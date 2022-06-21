package com.bigtreetc.sample.base.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldErrorResponseDto {

  String fieldName;

  Object rejectedValue;

  String errorMessage;
}
