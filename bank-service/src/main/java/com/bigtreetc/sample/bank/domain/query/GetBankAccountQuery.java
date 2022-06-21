package com.bigtreetc.sample.bank.domain.query;

import com.bigtreetc.sample.base.messaging.query.Query;
import java.io.Serial;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class GetBankAccountQuery implements Query {

  @Serial private static final long serialVersionUID = -2481963613152213180L;

  private UUID bankAccountId;
}
