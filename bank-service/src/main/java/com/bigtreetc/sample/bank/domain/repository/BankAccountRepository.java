package com.bigtreetc.sample.bank.domain.repository;

import com.bigtreetc.sample.bank.domain.model.BankAccount;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BankAccountRepository extends ReactiveCrudRepository<BankAccount, UUID> {

  Flux<BankAccount> findByBalanceGreaterThan(BigDecimal balance);
}
