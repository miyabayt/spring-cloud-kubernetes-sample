package com.bigtreetc.sample.eventstore.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

@Data
@Builder
public class R2dbcSnapshotEntity implements SnapshotEntity, Persistable<UUID> {

  @Id private UUID aggregateId;

  private Integer sequence;

  private String payload;

  private String metadata;

  @CreatedDate private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;

  @Override
  public UUID getId() {
    return this.aggregateId;
  }

  @Override
  public boolean isNew() {
    return this.createdAt == null;
  }
}
