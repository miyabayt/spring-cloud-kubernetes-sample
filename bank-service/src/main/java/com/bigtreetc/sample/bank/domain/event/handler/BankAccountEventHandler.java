package com.bigtreetc.sample.bank.domain.event.handler;

import com.bigtreetc.sample.bank.domain.event.*;
import com.bigtreetc.sample.bank.domain.model.BankAccount;
import com.bigtreetc.sample.bank.domain.projector.BankAccountProjector;
import com.bigtreetc.sample.base.eventhandling.EventHandler;
import com.bigtreetc.sample.base.eventstore.EventSourcingRepository;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class BankAccountEventHandler implements EventHandler {

  @NonNull final EventSourcingRepository eventSourcingRepository;

  @NonNull final BankAccountProjector bankAccountProjector;

  @Override
  public List<Class<? extends Event>> supports() {
    return List.of(
        BankAccountCreatedEvent.class,
        MoneyDepositedEvent.class,
        MoneyWithdrawnEvent.class,
        SourceBankAccountDebitedEvent.class,
        DestinationBankAccountCreditedEvent.class);
  }

  @Override
  public <E extends Event> Mono<Void> handle(E event, Metadata metadata) {
    switch (event) {
      case BankAccountCreatedEvent bankAccountCreatedEvent -> {
        return handle(bankAccountCreatedEvent);
      }
      case MoneyDepositedEvent moneyDepositedEvent -> {
        return handle(moneyDepositedEvent);
      }
      case MoneyWithdrawnEvent moneyWithdrawnEvent -> {
        return handle(moneyWithdrawnEvent);
      }
      case SourceBankAccountDebitedEvent sourceBankAccountDebitedEvent -> {
        return handle(sourceBankAccountDebitedEvent);
      }
      case DestinationBankAccountCreditedEvent destinationBankAccountCreditedEvent -> {
        return handle(destinationBankAccountCreditedEvent);
      }
      default -> throw new IllegalStateException("unknown event: " + event);
    }
  }

  private Mono<Void> handle(BankAccountCreatedEvent event) {
    val bankAccountId = event.getBankAccountId();
    return upsertBankAccount(bankAccountId);
  }

  private Mono<Void> handle(MoneyDepositedEvent event) {
    val bankAccountId = event.getBankAccountId();
    return upsertBankAccount(bankAccountId);
  }

  private Mono<Void> handle(MoneyWithdrawnEvent event) {
    val bankAccountId = event.getBankAccountId();
    return upsertBankAccount(bankAccountId);
  }

  private Mono<Void> handle(SourceBankAccountDebitedEvent event) {
    val bankAccountId = event.getSourceBankAccountId();
    return upsertBankAccount(bankAccountId);
  }

  private Mono<Void> handle(DestinationBankAccountCreditedEvent event) {
    val bankAccountId = event.getDestinationBankAccountId();
    return upsertBankAccount(bankAccountId);
  }

  private Mono<Void> upsertBankAccount(UUID bankAccountId) {
    return eventSourcingRepository
        .load(BankAccount.class, bankAccountId)
        .flatMap(bankAccountProjector::save);
  }
}
