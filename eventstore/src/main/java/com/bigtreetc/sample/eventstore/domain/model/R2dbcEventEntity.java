package com.bigtreetc.sample.eventstore.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

@Data
@Builder
public class R2dbcEventEntity implements EventEntity, Persistable<UUID> {

  @Id private UUID aggregateId;

  // @Id TODO: https://github.com/spring-projects/spring-data-r2dbc/issues/288
  private Integer sequence;

  private String eventType;

  private String payload;

  private String metadata;

  @CreatedDate private LocalDateTime createdAt;

  @CreatedBy private String createdBy;

  @Override
  public UUID getId() {
    return this.aggregateId;
  }

  @Override
  public boolean isNew() {
    return this.createdAt == null;
  }
}
