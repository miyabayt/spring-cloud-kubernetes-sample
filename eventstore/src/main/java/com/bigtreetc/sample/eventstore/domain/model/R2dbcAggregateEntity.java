package com.bigtreetc.sample.eventstore.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

@Data
@Builder
public class R2dbcAggregateEntity implements AggregateEntity, Persistable<UUID> {

  @Id private UUID aggregateId;

  private Integer sequence;

  @Override
  public UUID getId() {
    return this.aggregateId;
  }

  @Override
  public boolean isNew() {
    return this.aggregateId == null;
  }
}
