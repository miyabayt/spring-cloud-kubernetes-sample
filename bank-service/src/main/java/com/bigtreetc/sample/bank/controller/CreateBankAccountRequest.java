package com.bigtreetc.sample.bank.controller;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
public class CreateBankAccountRequest {

  @Min(0)
  private BigDecimal balance;

  @Min(0)
  private BigDecimal overdraftLimit;
}
