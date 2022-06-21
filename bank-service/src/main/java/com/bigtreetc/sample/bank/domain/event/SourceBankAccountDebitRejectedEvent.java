package com.bigtreetc.sample.bank.domain.event;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SourceBankAccountDebitRejectedEvent implements Event {

  @Serial private static final long serialVersionUID = 7633798674827971225L;

  private UUID sourceBankAccountId;

  private UUID destinationBankAccountId;

  private UUID bankTransferId;

  private String message;

  private LocalDateTime updatedAt;

  private String updatedBy;

  @Override
  public UUID getAggregateId() {
    return sourceBankAccountId;
  }
}
