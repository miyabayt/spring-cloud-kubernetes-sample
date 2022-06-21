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
public class BankTransferCreatedEvent implements Event {

  @Serial private static final long serialVersionUID = -8168347707469697573L;

  private UUID bankTransferId;

  private UUID sourceBankAccountId;

  private UUID destinationBankAccountId;

  private BigDecimal amount;

  private LocalDateTime createdAt;

  private String createdBy;

  @Override
  public UUID getAggregateId() {
    return bankTransferId;
  }
}
