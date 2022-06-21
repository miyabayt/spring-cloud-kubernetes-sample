package com.bigtreetc.sample.bank.domain.projector;

import com.bigtreetc.sample.bank.domain.model.BankTransfer;
import com.bigtreetc.sample.bank.domain.repository.BankTransferRepository;
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
public class BankTransferProjector {

  @NonNull final BankTransferRepository bankTransferRepository;

  public Mono<Void> save(BankTransfer bankTransfer) {
    val id = bankTransfer.getAggregateId();
    return bankTransferRepository.save(bankTransfer).then();
  }
}
