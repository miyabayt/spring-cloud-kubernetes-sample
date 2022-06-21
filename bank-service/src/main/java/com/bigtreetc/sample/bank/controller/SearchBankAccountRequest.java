package com.bigtreetc.sample.bank.controller;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchBankAccountRequest {

  private BigDecimal balanceGreaterThan;
}
