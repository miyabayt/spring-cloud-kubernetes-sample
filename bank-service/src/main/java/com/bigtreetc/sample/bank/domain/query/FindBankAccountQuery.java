package com.bigtreetc.sample.bank.domain.query;

import com.bigtreetc.sample.base.messaging.query.Query;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class FindBankAccountQuery implements Query {

  @Serial private static final long serialVersionUID = -3704330304670687742L;

  private BigDecimal balanceGreaterThan;
}
