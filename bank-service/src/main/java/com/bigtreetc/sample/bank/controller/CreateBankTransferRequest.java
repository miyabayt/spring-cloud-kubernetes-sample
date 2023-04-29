package com.bigtreetc.sample.bank.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankTransferRequest {

  @NotNull private UUID sourceBankAccountId;

  @NotNull private UUID destinationBankAccountId;

  @Min(100)
  private BigDecimal amount;
}
