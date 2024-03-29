package com.bigtreetc.sample.bank.domain.command;

import com.bigtreetc.sample.base.messaging.command.Command;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class CreateBankAccountCommand implements Command {

  @Serial private static final long serialVersionUID = -6566018167263440792L;

  @NotNull private UUID bankAccountId;

  @Min(0)
  private BigDecimal balance;

  @Min(0)
  private BigDecimal overdraftLimit;

  @Override
  public UUID getAggregateId() {
    return bankAccountId;
  }
}
