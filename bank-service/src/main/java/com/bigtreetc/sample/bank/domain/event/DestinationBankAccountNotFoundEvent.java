package com.bigtreetc.sample.bank.domain.event;

import com.bigtreetc.sample.base.messaging.event.Event;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DestinationBankAccountNotFoundEvent implements Event {

  @Serial private static final long serialVersionUID = 5414321420332129667L;

  private UUID sourceBankAccountId;

  private UUID destinationBankAccountId;

  private UUID bankTransferId;

  private BigDecimal amount;

  @Override
  public UUID getAggregateId() {
    return destinationBankAccountId;
  }
}
