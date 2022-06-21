package com.bigtreetc.sample.bank.domain.command;

import com.bigtreetc.sample.base.messaging.command.Command;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class ReturnMoneyOfFailedBankTransferCommand implements Command {

  @Serial private static final long serialVersionUID = -257191909185816480L;

  @NotNull private UUID sourceBankAccountId;

  @NotNull private UUID destinationBankAccountId;

  @Min(1)
  private BigDecimal amount;

  @Override
  public UUID getAggregateId() {
    return sourceBankAccountId;
  }
}
