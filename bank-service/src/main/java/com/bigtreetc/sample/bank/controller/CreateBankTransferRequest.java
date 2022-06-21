package com.bigtreetc.sample.bank.controller;

import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
