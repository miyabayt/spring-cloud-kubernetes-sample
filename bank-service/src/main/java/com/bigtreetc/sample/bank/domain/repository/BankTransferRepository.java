package com.bigtreetc.sample.bank.domain.repository;

import com.bigtreetc.sample.bank.domain.model.BankTransfer;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransferRepository extends ReactiveCrudRepository<BankTransfer, UUID> {}
