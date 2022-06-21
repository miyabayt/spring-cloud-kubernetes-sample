package com.bigtreetc.sample.bank.domain.event.handler;

import com.bigtreetc.sample.bank.domain.event.BankTransferCompletedEvent;
import com.bigtreetc.sample.bank.domain.event.BankTransferCreatedEvent;
import com.bigtreetc.sample.bank.domain.event.BankTransferFailedEvent;
import com.bigtreetc.sample.bank.domain.model.BankTransfer;
import com.bigtreetc.sample.bank.domain.projector.BankTransferProjector;
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
public class BankTransferEventHandler implements EventHandler {

  @NonNull final EventSourcingRepository eventSourcingRepository;

  @NonNull final BankTransferProjector bankTransferProjector;

  @Override
  public List<Class<? extends Event>> supports() {
    return List.of(
        BankTransferCreatedEvent.class,
        BankTransferCompletedEvent.class,
        BankTransferFailedEvent.class);
  }

  @Override
  public <E extends Event> Mono<Void> handle(E event, Metadata metadata) {
    switch (event) {
      case BankTransferCreatedEvent bankTransferCreatedEvent -> {
        return handle(bankTransferCreatedEvent);
      }
      case BankTransferCompletedEvent bankTransferCompletedEvent -> {
        return handle(bankTransferCompletedEvent);
      }
      case BankTransferFailedEvent bankTransferFailedEvent -> {
        return handle(bankTransferFailedEvent);
      }
      default -> throw new IllegalStateException("unknown event: " + event);
    }
  }

  private Mono<Void> handle(BankTransferCreatedEvent event) {
    val bankTransferId = event.getBankTransferId();
    return upsertBankTransfer(bankTransferId);
  }

  private Mono<Void> handle(BankTransferCompletedEvent event) {
    val bankTransferId = event.getBankTransferId();
    return upsertBankTransfer(bankTransferId);
  }

  private Mono<Void> handle(BankTransferFailedEvent event) {
    val bankTransferId = event.getBankTransferId();
    return upsertBankTransfer(bankTransferId);
  }

  private Mono<Void> upsertBankTransfer(UUID bankTransferId) {
    return eventSourcingRepository
        .load(BankTransfer.class, bankTransferId)
        .flatMap(bankTransferProjector::save);
  }
}
