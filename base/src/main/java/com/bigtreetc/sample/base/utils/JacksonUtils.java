package com.bigtreetc.sample.base.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtils {

  private static final ObjectMapper OBJECT_MAPPER =
      JsonMapper.builder()
          .findAndAddModules()
          .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .build();

  public static TypeFactory getTypeFactory() {
    return OBJECT_MAPPER.getTypeFactory();
  }

  @SneakyThrows
  public static <T> T readValue(String content, Class<T> valueType) {
    return OBJECT_MAPPER.readValue(content, valueType);
  }

  @SneakyThrows
  public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
    return OBJECT_MAPPER.readValue(content, valueTypeRef);
  }

  @SneakyThrows
  public static <T> T readValue(String content, CollectionLikeType collectionLikeType) {
    return OBJECT_MAPPER.readValue(content, collectionLikeType);
  }

  @SneakyThrows
  public static String writeValueAsString(Object value) {
    return OBJECT_MAPPER.writeValueAsString(value);
  }
}
