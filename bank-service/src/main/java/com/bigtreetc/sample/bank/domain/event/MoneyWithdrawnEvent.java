package com.bigtreetc.sample.bank.domain.event;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MoneyWithdrawnEvent implements Event {

  @Serial private static final long serialVersionUID = -4580275336934117876L;

  private UUID bankAccountId;

  private BigDecimal amount;

  private LocalDateTime updatedAt;

  private String updatedBy;

  @Override
  public UUID getAggregateId() {
    return bankAccountId;
  }
}
