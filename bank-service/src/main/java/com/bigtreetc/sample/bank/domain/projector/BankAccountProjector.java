package com.bigtreetc.sample.bank.domain.projector;

import com.bigtreetc.sample.bank.domain.model.BankAccount;
import com.bigtreetc.sample.bank.domain.repository.BankAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
@Slf4j
public class BankAccountProjector {

  @NonNull final BankAccountRepository bankAccountRepository;

  public Mono<Void> save(BankAccount bankAccount) {
    val id = bankAccount.getAggregateId();
    return bankAccountRepository.save(bankAccount).then();
  }
}
