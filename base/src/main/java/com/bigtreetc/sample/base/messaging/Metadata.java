package com.bigtreetc.sample.base.messaging;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
@Setter
public class Metadata {

  private static final String SAGA_ID_KEY = "sagaId";

  @JsonIgnore private final Map<String, Object> values;

  public Metadata() {
    values = new HashMap<>();
  }

  public Metadata(Map<String, Object> fromValues) {
    values = new HashMap<>(fromValues);
  }

  public static Metadata from(Map<String, Object> fromValues) {
    if (fromValues instanceof Metadata) {
      return (Metadata) fromValues;
    } else if (fromValues == null || fromValues.isEmpty()) {
      return new Metadata();
    }
    return new Metadata(fromValues);
  }

  public static Metadata with(String key, Object value) {
    return Metadata.from(Map.of(key, value));
  }

  public Metadata and(String key, Object value) {
    Map<String, Object> newValues = new HashMap<>(values);
    newValues.put(key, value);
    return new Metadata(newValues);
  }

  public Object get(String key) {
    return this.values.get(key);
  }

  public boolean containsKey(String key) {
    return this.values.containsKey(key);
  }

  public void put(String key, Object value) {
    this.values.put(key, value);
  }

  public Metadata andSagaId(UUID sagaId) {
    Map<String, Object> newValues = new HashMap<>(this.values);
    newValues.put(SAGA_ID_KEY, sagaId.toString());
    return new Metadata(newValues);
  }

  @JsonIgnore
  public UUID getSagaId() {
    val sagaId = this.values.get(SAGA_ID_KEY);
    if (sagaId == null) {
      return null;
    }
    return UUID.fromString((String) sagaId);
  }

  @JsonAnyGetter
  public Map<String, Object> getValues() {
    return this.values;
  }

  @JsonAnySetter
  public void setValues(String key, Object value) {
    this.values.put(key, value);
  }
}
