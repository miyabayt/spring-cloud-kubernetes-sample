package com.bigtreetc.sample.bank.domain.command.handler;

import com.bigtreetc.sample.bank.domain.command.*;
import com.bigtreetc.sample.bank.domain.event.BankTransferCompletedEvent;
import com.bigtreetc.sample.bank.domain.event.BankTransferCreatedEvent;
import com.bigtreetc.sample.bank.domain.event.BankTransferFailedEvent;
import com.bigtreetc.sample.bank.domain.model.BankTransfer;
import com.bigtreetc.sample.base.commandhandling.CommandHandler;
import com.bigtreetc.sample.base.eventstore.EventSourcingRepository;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.command.Command;
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
public class BankTransferCommandHandler implements CommandHandler {

  @NonNull final EventSourcingRepository eventSourcingRepository;

  @NonNull final SagaManager sagaManager;

  @Override
  public List<Class<? extends Command>> supports() {
    return List.of(
        CreateBankTransferCommand.class,
        MarkBankTransferCompletedCommand.class,
        MarkBankTransferFailedCommand.class);
  }

  @Override
  public <C extends Command> Mono<Void> handle(C command, Metadata metadata) {
    switch (command) {
      case CreateBankTransferCommand createBankTransferCommand -> {
        return handle(createBankTransferCommand, metadata);
      }
      case MarkBankTransferCompletedCommand markBankTransferCompletedCommand -> {
        return handle(markBankTransferCompletedCommand, metadata);
      }
      case MarkBankTransferFailedCommand markBankTransferFailedCommand -> {
        return handle(markBankTransferFailedCommand, metadata);
      }
      default -> throw new IllegalStateException("unknown command: " + command);
    }
  }

  private Mono<Void> handle(CreateBankTransferCommand command, Metadata metadata) {
    val event =
        BankTransferCreatedEvent.builder()
            .bankTransferId(command.getBankTransferId())
            .sourceBankAccountId(command.getSourceBankAccountId())
            .destinationBankAccountId(command.getDestinationBankAccountId())
            .amount(command.getAmount())
            .createdAt(LocalDateTime.now())
            .createdBy("TODO")
            .build();
    val bankTransfer = new BankTransfer();
    bankTransfer.apply(event, metadata);
    return eventSourcingRepository
        .save(bankTransfer, metadata)
        .then(sagaManager.startSaga(event, metadata))
        .then();
  }

  private Mono<Void> handle(MarkBankTransferCompletedCommand command, Metadata metadata) {
    val bankTransferId = command.getBankTransferId();
    return eventSourcingRepository
        .load(BankTransfer.class, bankTransferId)
        .flatMap(
            bankTransfer -> {
              val event =
                  BankTransferCompletedEvent.builder()
                      .bankTransferId(bankTransferId)
                      .updatedAt(LocalDateTime.now())
                      .updatedBy("TODO")
                      .build();
              bankTransfer.apply(event, metadata);
              return eventSourcingRepository
                  .save(bankTransfer, metadata)
                  .then(sagaManager.endSaga(event, metadata));
            });
  }

  private Mono<Void> handle(MarkBankTransferFailedCommand command, Metadata metadata) {
    val bankTransferId = command.getBankTransferId();
    return eventSourcingRepository
        .load(BankTransfer.class, bankTransferId)
        .flatMap(
            bankTransfer -> {
              val event =
                  BankTransferFailedEvent.builder()
                      .bankTransferId(bankTransferId)
                      .updatedAt(LocalDateTime.now())
                      .updatedBy("TODO")
                      .build();
              bankTransfer.apply(event, metadata);
              return eventSourcingRepository
                  .save(bankTransfer, metadata)
                  .then(sagaManager.endSaga(event, metadata));
            });
  }
}
