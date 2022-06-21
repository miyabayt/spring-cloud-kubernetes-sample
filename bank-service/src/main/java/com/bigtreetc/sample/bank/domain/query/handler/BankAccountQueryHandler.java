package com.bigtreetc.sample.bank.domain.query.handler;

import com.bigtreetc.sample.bank.domain.model.BankAccount;
import com.bigtreetc.sample.bank.domain.query.FindBankAccountQuery;
import com.bigtreetc.sample.bank.domain.query.GetBankAccountQuery;
import com.bigtreetc.sample.bank.domain.repository.BankAccountRepository;
import com.bigtreetc.sample.base.messaging.query.Query;
import com.bigtreetc.sample.base.model.Aggregate;
import com.bigtreetc.sample.base.queryhandling.QueryHandler;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class BankAccountQueryHandler implements QueryHandler {

  @NonNull final BankAccountRepository bankAccountRepository;

  @Override
  public List<Class<? extends Query>> supports() {
    return List.of(FindBankAccountQuery.class, GetBankAccountQuery.class);
  }

  @Override
  public <C extends Query> CorePublisher<? extends Aggregate> handle(C query) {
    switch (query) {
      case GetBankAccountQuery getBankAccountQuery -> {
        return handle(getBankAccountQuery);
      }
      case FindBankAccountQuery findBankAccountQuery -> {
        return handle(findBankAccountQuery);
      }
      default -> throw new IllegalStateException("unknown query: " + query);
    }
  }

  private Flux<BankAccount> handle(FindBankAccountQuery query) {
    val balanceMoreThan = query.getBalanceGreaterThan();
    return bankAccountRepository.findByBalanceGreaterThan(balanceMoreThan);
  }

  private Mono<BankAccount> handle(GetBankAccountQuery query) {
    val bankAccountId = query.getBankAccountId();
    return bankAccountRepository.findById(bankAccountId);
  }
}
