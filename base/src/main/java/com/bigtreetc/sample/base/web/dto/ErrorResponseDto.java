package com.bigtreetc.sample.base.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDto {

  String requestId;

  @JsonInclude(Include.NON_NULL)
  List<FieldErrorResponseDto> fieldErrors;

  String message;

  @Builder.Default boolean success = false;
}
