package com.bigtreetc.sample.bank.controller;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
public class CreateBankAccountRequest {

  @Min(0)
  private BigDecimal balance;

  @Min(0)
  private BigDecimal overdraftLimit;
}
