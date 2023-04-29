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
public class CreditDestinationBankAccountCommand implements Command {

  @Serial private static final long serialVersionUID = -1751992822538117381L;

  @NotNull private UUID sourceBankAccountId;

  @NotNull private UUID destinationBankAccountId;

  @NotNull private UUID bankTransferId;

  @Min(1)
  private BigDecimal amount;

  @Override
  public UUID getAggregateId() {
    return destinationBankAccountId;
  }
}
