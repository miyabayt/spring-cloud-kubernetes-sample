package com.bigtreetc.sample.bank.domain.event;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BankTransferFailedEvent implements Event {

  @Serial private static final long serialVersionUID = -458882702417064442L;

  private UUID bankTransferId;

  private LocalDateTime updatedAt;

  private String updatedBy;

  @Override
  public UUID getAggregateId() {
    return bankTransferId;
  }
}
