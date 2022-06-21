package com.bigtreetc.sample.bank.domain.command.handler;

import com.bigtreetc.sample.bank.domain.command.*;
import com.bigtreetc.sample.bank.domain.event.BankAccountCreatedEvent;
import com.bigtreetc.sample.bank.domain.event.DestinationBankAccountCreditedEvent;
import com.bigtreetc.sample.bank.domain.event.MoneyDepositedEvent;
import com.bigtreetc.sample.bank.domain.event.MoneyWithdrawnEvent;
import com.bigtreetc.sample.bank.domain.event.SourceBankAccountDebitRejectedEvent;
import com.bigtreetc.sample.bank.domain.event.SourceBankAccountDebitedEvent;
import com.bigtreetc.sample.bank.domain.event.WithdrawMoneyRejectedEvent;
import com.bigtreetc.sample.bank.domain.model.BankAccount;
import com.bigtreetc.sample.base.commandhandling.CommandHandler;
import com.bigtreetc.sample.base.eventstore.EventSourcingRepository;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.command.Command;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.messaging.event.EventBus;
import com.bigtreetc.sample.base.messaging.saga.SagaManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class BankAccountCommandHandler implements CommandHandler {

  @NonNull final EventSourcingRepository eventSourcingRepository;

  @NonNull final EventBus eventBus;

  @NonNull final SagaManager sagaManager;

  @Override
  public List<Class<? extends Command>> supports() {
    return List.of(
        CreateBankAccountCommand.class,
        DepositMoneyCommand.class,
        WithdrawMoneyCommand.class,
        DebitSourceBankAccountCommand.class,
        CreditDestinationBankAccountCommand.class);
  }

  @Override
  public <C extends Command> Mono<Void> handle(C command, Metadata metadata) {
    switch (command) {
      case CreateBankAccountCommand createBankAccountCommand -> {
        return handle(createBankAccountCommand, metadata);
      }
      case DepositMoneyCommand depositMoneyCommand -> {
        return handle(depositMoneyCommand, metadata);
      }
      case WithdrawMoneyCommand withdrawMoneyCommand -> {
        return handle(withdrawMoneyCommand, metadata);
      }
      case DebitSourceBankAccountCommand debitSourceBankAccountCommand -> {
        return handle(debitSourceBankAccountCommand, metadata);
      }
      case CreditDestinationBankAccountCommand creditDestinationBankAccountCommand -> {
        return handle(creditDestinationBankAccountCommand, metadata);
      }
      default -> throw new IllegalStateException("unknown command: " + command);
    }
  }

  private Mono<Void> handle(CreateBankAccountCommand command, Metadata metadata) {
    val bankAccount = new BankAccount();
    bankAccount.apply(
        BankAccountCreatedEvent.builder()
            .bankAccountId(command.getBankAccountId())
            .balance(command.getBalance())
            .overdraftLimit(command.getOverdraftLimit())
            .createdAt(LocalDateTime.now())
            .createdBy("TODO")
            .build(),
        metadata);
    return eventSourcingRepository.save(bankAccount, metadata).then();
  }

  private Mono<Void> handle(DepositMoneyCommand command, Metadata metadata) {
    val bankAccountId = command.getBankAccountId();
    return eventSourcingRepository
        .load(BankAccount.class, bankAccountId)
        .flatMap(
            bankAccount -> {
              val event =
                  MoneyDepositedEvent.builder()
                      .bankAccountId(bankAccountId)
                      .amount(command.getAmount())
                      .updatedAt(LocalDateTime.now())
                      .updatedBy("TODO")
                      .build();
              bankAccount.apply(event, metadata);
              return eventSourcingRepository.save(bankAccount, metadata).then();
            });
  }

  private Mono<Void> handle(WithdrawMoneyCommand command, Metadata metadata) {
    val bankAccountId = command.getBankAccountId();
    return eventSourcingRepository
        .load(BankAccount.class, bankAccountId)
        .flatMap(
            bankAccount -> {
              Event event;
              val balance = bankAccount.getBalance();
              val overdraftLimit = bankAccount.getOverdraftLimit();
              if (balance.compareTo(command.getAmount().add(overdraftLimit)) < 0) {
                log.info(
                    "withdraw rejected. [balance=%d, withdraw=%d, overdraft=%d]"
                        .formatted(
                            balance.toBigInteger(),
                            command.getAmount().toBigInteger(),
                            overdraftLimit.toBigInteger()));
                event =
                    WithdrawMoneyRejectedEvent.builder()
                        .bankAccountId(bankAccountId)
                        .amount(command.getAmount())
                        .updatedAt(LocalDateTime.now())
                        .updatedBy("TODO")
                        .build();
              } else {
                event =
                    MoneyWithdrawnEvent.builder()
                        .bankAccountId(bankAccountId)
                        .amount(command.getAmount())
                        .updatedAt(LocalDateTime.now())
                        .updatedBy("TODO")
                        .build();
              }
              bankAccount.apply(event, metadata);
              return eventSourcingRepository.save(bankAccount, metadata).then();
            });
  }

  private Mono<Void> handle(DebitSourceBankAccountCommand command, Metadata metadata) {
    val sourceBankAccountId = command.getSourceBankAccountId();
    val destinationBankAccountId = command.getDestinationBankAccountId();
    return eventSourcingRepository
        .load(BankAccount.class, sourceBankAccountId)
        .flatMap(
            bankAccount -> {
              Event event;
              val balance = bankAccount.getBalance();
              val overdraftLimit = bankAccount.getOverdraftLimit();
              if (balance.compareTo(command.getAmount().add(overdraftLimit)) < 0) {
                log.info(
                    "debit source bank account rejected. [balance=%d, withdraw=%d, overdraft=%d]"
                        .formatted(
                            balance.toBigInteger(),
                            command.getAmount().toBigInteger(),
                            overdraftLimit.toBigInteger()));
                event =
                    SourceBankAccountDebitRejectedEvent.builder()
                        .bankTransferId(command.getBankTransferId())
                        .sourceBankAccountId(sourceBankAccountId)
                        .destinationBankAccountId(destinationBankAccountId)
                        .message("insufficient balance")
                        .updatedAt(LocalDateTime.now())
                        .updatedBy("TODO")
                        .build();
              } else {
                event =
                    SourceBankAccountDebitedEvent.builder()
                        .bankTransferId(command.getBankTransferId())
                        .sourceBankAccountId(sourceBankAccountId)
                        .destinationBankAccountId(destinationBankAccountId)
                        .amount(command.getAmount())
                        .updatedAt(LocalDateTime.now())
                        .updatedBy("TODO")
                        .build();
              }
              bankAccount.apply(event, metadata);
              return eventSourcingRepository
                  .save(bankAccount, metadata)
                  .then(sagaManager.nextStep(event, metadata));
            });
  }

  private Mono<Void> handle(CreditDestinationBankAccountCommand command, Metadata metadata) {
    val sourceBankAccountId = command.getSourceBankAccountId();
    val destinationBankAccountId = command.getDestinationBankAccountId();
    return eventSourcingRepository
        .load(BankAccount.class, destinationBankAccountId)
        .flatMap(
            bankAccount -> {
              val event =
                  DestinationBankAccountCreditedEvent.builder()
                      .bankTransferId(command.getBankTransferId())
                      .sourceBankAccountId(sourceBankAccountId)
                      .destinationBankAccountId(destinationBankAccountId)
                      .amount(command.getAmount())
                      .updatedAt(LocalDateTime.now())
                      .updatedBy("TODO")
                      .build();
              bankAccount.apply(event, metadata);
              return eventSourcingRepository
                  .save(bankAccount, metadata)
                  .then(sagaManager.nextStep(event, metadata));
            })
        .switchIfEmpty(
            Mono.defer(
                () -> {
                  return Mono.empty();
                }));
  }
}
