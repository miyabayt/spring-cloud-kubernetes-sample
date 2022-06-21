package com.bigtreetc.sample.bank.controller;

import com.bigtreetc.sample.bank.domain.command.CreateBankAccountCommand;
import com.bigtreetc.sample.bank.domain.command.DepositMoneyCommand;
import com.bigtreetc.sample.bank.domain.command.WithdrawMoneyCommand;
import com.bigtreetc.sample.bank.domain.model.BankAccount;
import com.bigtreetc.sample.bank.domain.query.FindBankAccountQuery;
import com.bigtreetc.sample.bank.domain.query.GetBankAccountQuery;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bank-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankAccountController {

  @NonNull final CommandGateway commandGateway;

  @NonNull final QueryGateway queryGateway;

  @NonNull final EventSourcingRepository eventSourcingRepository;

  /**
   * 銀行口座情報を検索します。
   *
   * @return
   */
  @GetMapping
  public Flux<BankAccount> searchBankAccount(@Validated SearchBankAccountRequest request) {
    val query =
        FindBankAccountQuery.builder().balanceGreaterThan(request.getBalanceGreaterThan()).build();
    return queryGateway.findMany(BankAccount.class, query);
  }

  /**
   * 銀行口座情報を取得します。
   *
   * @return
   */
  @GetMapping("/{id}")
  public Mono<BankAccount> getBankAccount(@PathVariable UUID id) {
    val query = GetBankAccountQuery.builder().bankAccountId(id).build();
    return queryGateway.findOne(BankAccount.class, query);
  }

  /**
   * 銀行口座を作成します。
   *
   * @return
   */
  @PostMapping
  public Mono<BankAccount> createBankAccount(
      @Validated @RequestBody CreateBankAccountRequest request) {
    val bankAccountId = UUID.randomUUID();
    val command =
        CreateBankAccountCommand.builder()
            .bankAccountId(bankAccountId)
            .balance(request.getBalance())
            .overdraftLimit(request.getOverdraftLimit())
            .build();
    return commandGateway
        .sendAndWait(command)
        .flatMap(
            result -> eventSourcingRepository.load(BankAccount.class, result.getAggregateId()));
  }

  /**
   * 銀行口座に預金します。
   *
   * @return
   */
  @PostMapping("/deposit")
  public Mono<BankAccount> depositMoney(@Validated @RequestBody DepositMoneyRequest request) {
    val command =
        DepositMoneyCommand.builder()
            .bankAccountId(request.getBankAccountId())
            .amount(request.getAmount())
            .build();
    return commandGateway
        .sendAndWait(command)
        .flatMap(
            result -> eventSourcingRepository.load(BankAccount.class, result.getAggregateId()));
  }

  /**
   * 銀行口座から出金します。
   *
   * @return
   */
  @PostMapping("/withdraw")
  public Mono<BankAccount> withdrawMoney(@Validated @RequestBody WithdrawMoneyRequest request) {
    val command =
        WithdrawMoneyCommand.builder()
            .bankAccountId(request.getBankAccountId())
            .amount(request.getAmount())
            .build();
    return commandGateway
        .sendAndWait(command)
        .flatMap(
            result -> eventSourcingRepository.load(BankAccount.class, result.getAggregateId()));
  }
}
