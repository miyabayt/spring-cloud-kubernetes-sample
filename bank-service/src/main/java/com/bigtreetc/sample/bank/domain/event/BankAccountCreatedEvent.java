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
public class BankAccountCreatedEvent implements Event {

  @Serial private static final long serialVersionUID = -1595572808209568892L;

  private UUID bankAccountId;

  private BigDecimal balance;

  private BigDecimal overdraftLimit;

  private LocalDateTime createdAt;

  private String createdBy;

  @Override
  public UUID getAggregateId() {
    return bankAccountId;
  }
}
