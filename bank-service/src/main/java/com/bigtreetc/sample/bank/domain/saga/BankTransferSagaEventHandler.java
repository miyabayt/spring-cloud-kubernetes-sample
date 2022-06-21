package com.bigtreetc.sample.bank.domain.saga;

import com.bigtreetc.sample.bank.domain.command.*;
import com.bigtreetc.sample.bank.domain.event.*;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.command.CommandBus;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.messaging.saga.SagaEventHandler;
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
public class BankTransferSagaEventHandler implements SagaEventHandler {

  @NonNull final CommandBus commandBus;

  @Override
  public List<Class<? extends Event>> supports() {
    return List.of(
        BankTransferCreatedEvent.class,
        SourceBankAccountNotFoundEvent.class,
        SourceBankAccountDebitedEvent.class,
        SourceBankAccountDebitRejectedEvent.class,
        DestinationBankAccountNotFoundEvent.class,
        DestinationBankAccountCreditedEvent.class);
  }

  @Override
  public <E extends Event> Mono<Void> handle(E event, Metadata metadata) {
    switch (event) {
      case BankTransferCreatedEvent bankTransferCreatedEvent -> {
        return handle(bankTransferCreatedEvent, metadata);
      }
      case SourceBankAccountNotFoundEvent sourceBankAccountNotFoundEvent -> {
        return handle(sourceBankAccountNotFoundEvent, metadata);
      }
      case SourceBankAccountDebitedEvent sourceBankAccountDebitedEvent -> {
        return handle(sourceBankAccountDebitedEvent, metadata);
      }
      case SourceBankAccountDebitRejectedEvent sourceBankAccountDebitRejectedEvent -> {
        return handle(sourceBankAccountDebitRejectedEvent, metadata);
      }
      case DestinationBankAccountNotFoundEvent destinationBankAccountNotFoundEvent -> {
        return handle(destinationBankAccountNotFoundEvent, metadata);
      }
      case DestinationBankAccountCreditedEvent destinationBankAccountCreditedEvent -> {
        return handle(destinationBankAccountCreditedEvent, metadata);
      }
      default -> throw new IllegalStateException("unknown event: " + event);
    }
  }

  private Mono<Void> handle(BankTransferCreatedEvent event, Metadata metadata) {
    return Mono.defer(
        () -> {
          val command =
              DebitSourceBankAccountCommand.builder()
                  .sourceBankAccountId(event.getSourceBankAccountId())
                  .destinationBankAccountId(event.getDestinationBankAccountId())
                  .bankTransferId(event.getBankTransferId())
                  .amount(event.getAmount())
                  .build();
          val commandMessage = command.toCommandMessage(metadata);
          return commandBus.send(commandMessage).then();
        });
  }

  private Mono<Void> handle(SourceBankAccountNotFoundEvent event, Metadata metadata) {
    return Mono.defer(
        () -> {
          val command =
              MarkBankTransferFailedCommand.builder()
                  .bankTransferId(event.getBankTransferId())
                  .updatedAt(LocalDateTime.now())
                  .updatedBy("TODO")
                  .build();
          val commandMessage = command.toCommandMessage(metadata);
          return commandBus.send(commandMessage).then();
        });
  }

  private Mono<Void> handle(SourceBankAccountDebitedEvent event, Metadata metadata) {
    return Mono.defer(
        () -> {
          val command =
              CreditDestinationBankAccountCommand.builder()
                  .bankTransferId(event.getBankTransferId())
                  .sourceBankAccountId(event.getSourceBankAccountId())
                  .destinationBankAccountId(event.getDestinationBankAccountId())
                  .amount(event.getAmount())
                  .build();
          val commandMessage = command.toCommandMessage(metadata);
          return commandBus.send(commandMessage).then();
        });
  }

  private Mono<Void> handle(SourceBankAccountDebitRejectedEvent event, Metadata metadata) {
    return Mono.defer(
        () -> {
          val command =
              MarkBankTransferFailedCommand.builder()
                  .bankTransferId(event.getBankTransferId())
                  .updatedAt(LocalDateTime.now())
                  .updatedBy("TODO")
                  .build();
          val commandMessage = command.toCommandMessage(metadata);
          return commandBus.send(commandMessage).then();
        });
  }

  private Mono<Void> handle(DestinationBankAccountNotFoundEvent event, Metadata metadata) {
    return Mono.defer(
        () -> {
          val command =
              ReturnMoneyOfFailedBankTransferCommand.builder()
                  .sourceBankAccountId(event.getSourceBankAccountId())
                  .destinationBankAccountId(event.getDestinationBankAccountId())
                  .amount(event.getAmount())
                  .build();
          val commandMessage = command.toCommandMessage(metadata);
          return commandBus.send(commandMessage).then();
        });
  }

  private Mono<Void> handle(DestinationBankAccountCreditedEvent event, Metadata metadata) {
    return Mono.defer(
        () -> {
          val command =
              MarkBankTransferCompletedCommand.builder()
                  .bankTransferId(event.getBankTransferId())
                  .build();
          val commandMessage = command.toCommandMessage(metadata);
          return commandBus.send(commandMessage).then();
        });
  }
}
