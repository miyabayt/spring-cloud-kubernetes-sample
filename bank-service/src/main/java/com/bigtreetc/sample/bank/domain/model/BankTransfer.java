package com.bigtreetc.sample.bank.domain.model;

import com.bigtreetc.sample.bank.domain.event.BankTransferCompletedEvent;
import com.bigtreetc.sample.bank.domain.event.BankTransferCreatedEvent;
import com.bigtreetc.sample.bank.domain.event.BankTransferFailedEvent;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.model.AggregateRoot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Jacksonized
@SuperBuilder
@Table(value = "bank_transfers")
public class BankTransfer extends AggregateRoot implements Persistable<UUID> {

  @Id private UUID id;

  private UUID sourceBankAccountId;

  private UUID destinationBankAccountId;

  private BigDecimal amount;

  private BankTransferStatus status;

  private LocalDateTime createdAt;

  private String createdBy;

  private LocalDateTime updatedAt;

  private String updatedBy;

  @Override
  public UUID getAggregateId() {
    return this.id;
  }

  @Override
  public <E extends Event> void handle(E event, Metadata metadata) {
    switch (event) {
      case BankTransferCreatedEvent bankTransferCreatedEvent -> handle(bankTransferCreatedEvent);
      case BankTransferCompletedEvent bankTransferCompletedEvent -> handle(
          bankTransferCompletedEvent);
      case BankTransferFailedEvent bankTransferFailedEvent -> handle(bankTransferFailedEvent);
      default -> throw new IllegalStateException("unknown event: " + event);
    }
  }

  private void handle(BankTransferCreatedEvent event) {
    this.id = event.getAggregateId();
    this.sourceBankAccountId = event.getSourceBankAccountId();
    this.destinationBankAccountId = event.getDestinationBankAccountId();
    this.amount = event.getAmount();
    this.status = BankTransferStatus.STARTED;
    this.createdAt = event.getCreatedAt();
    this.createdBy = event.getCreatedBy();
  }

  private void handle(BankTransferCompletedEvent event) {
    this.status = BankTransferStatus.COMPLETED;
    this.updatedAt = event.getUpdatedAt();
    this.updatedBy = event.getUpdatedBy();
  }

  private void handle(BankTransferFailedEvent event) {
    this.status = BankTransferStatus.FAILED;
    this.updatedAt = event.getUpdatedAt();
    this.updatedBy = event.getUpdatedBy();
  }

  @JsonIgnore
  @Override
  public boolean isNew() {
    return this.updatedAt == null;
  }
}
