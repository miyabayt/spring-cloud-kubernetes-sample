package com.bigtreetc.sample.bank.domain.command;

import com.bigtreetc.sample.base.messaging.command.Command;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class MarkBankTransferCompletedCommand implements Command {

  @Serial private static final long serialVersionUID = 3349248271418126685L;

  @NotNull private UUID bankTransferId;

  @Override
  public UUID getAggregateId() {
    return bankTransferId;
  }
}
