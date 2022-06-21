package com.bigtreetc.sample.bank.controller;

import com.bigtreetc.sample.bank.domain.command.CreateBankTransferCommand;
import com.bigtreetc.sample.bank.domain.model.BankTransfer;
import com.bigtreetc.sample.base.eventstore.EventSourcingRepository;
import com.bigtreetc.sample.base.messaging.command.CommandGateway;
import com.bigtreetc.sample.base.messaging.query.QueryGateway;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bank-transfers", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankTransferController {

  @NonNull final CommandGateway commandGateway;

  @NonNull final QueryGateway queryGateway;

  @NonNull final EventSourcingRepository eventSourcingRepository;

  /**
   * 振込します。
   *
   * @return
   */
  @PostMapping
  public Mono<BankTransfer> createBankTransfer(
      @Validated @RequestBody CreateBankTransferRequest request) {
    val bankTransferId = UUID.randomUUID();
    val command =
        CreateBankTransferCommand.builder()
            .bankTransferId(bankTransferId)
            .amount(request.getAmount())
            .sourceBankAccountId(request.getSourceBankAccountId())
            .destinationBankAccountId(request.getDestinationBankAccountId())
            .build();
    return commandGateway
        .sendAndWait(command)
        .flatMap(
            result -> eventSourcingRepository.load(BankTransfer.class, result.getAggregateId()));
  }
}
