package com.bigtreetc.sample.bank.domain.event;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.io.Serial;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SourceBankAccountNotFoundEvent implements Event {

  @Serial private static final long serialVersionUID = -1663862843483486398L;

  private UUID sourceBankAccountId;

  private UUID destinationBankAccountId;

  private UUID bankTransferId;

  @Override
  public UUID getAggregateId() {
    return sourceBankAccountId;
  }
}
