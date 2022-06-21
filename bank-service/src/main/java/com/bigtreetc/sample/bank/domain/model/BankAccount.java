package com.bigtreetc.sample.bank.domain.model;

import com.bigtreetc.sample.bank.domain.event.*;
import com.bigtreetc.sample.base.messaging.Metadata;
import com.bigtreetc.sample.base.messaging.event.Event;
import com.bigtreetc.sample.base.model.AggregateRoot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Jacksonized
@SuperBuilder
@Table(value = "bank_accounts")
public class BankAccount extends AggregateRoot implements Persistable<UUID> {

  @Id private UUID id;

  private BigDecimal balance;

  private BigDecimal overdraftLimit;

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
      case BankAccountCreatedEvent bankAccountCreatedEvent -> handle(bankAccountCreatedEvent);
      case MoneyDepositedEvent moneyDepositedEvent -> handle(moneyDepositedEvent);
      case MoneyWithdrawnEvent moneyWithdrawnEvent -> handle(moneyWithdrawnEvent);
      case SourceBankAccountDebitedEvent sourceBankAccountDebitedEvent -> handle(
          sourceBankAccountDebitedEvent);
      case DestinationBankAccountCreditedEvent destinationBankAccountCreditedEvent -> handle(
          destinationBankAccountCreditedEvent);
      default -> throw new IllegalStateException("unknown event: " + event);
    }
  }

  public void handle(BankAccountCreatedEvent event) {
    this.id = event.getAggregateId();
    this.balance = event.getBalance();
    this.overdraftLimit = event.getOverdraftLimit();
    this.createdAt = event.getCreatedAt();
    this.createdBy = event.getCreatedBy();
  }

  public void handle(MoneyDepositedEvent event) {
    // 預金した場合は、残高を加算する
    if (this.balance == null) {
      this.balance = BigDecimal.ZERO;
    }
    val amountToAdd = event.getAmount();
    if (amountToAdd != null) {
      this.balance = this.balance.add(amountToAdd);
    }
    this.updatedAt = event.getUpdatedAt();
    this.updatedBy = event.getUpdatedBy();
  }

  public void handle(MoneyWithdrawnEvent event) {
    // 出金した場合は、残高を減算する
    if (this.balance == null) {
      this.balance = BigDecimal.ZERO;
    }
    val amountToSubtract = event.getAmount();
    if (amountToSubtract != null) {
      this.balance = this.balance.subtract(amountToSubtract);
    }
    this.updatedAt = event.getUpdatedAt();
    this.updatedBy = event.getUpdatedBy();
  }

  public void handle(SourceBankAccountDebitedEvent event) {
    // 出金した場合は、残高を減算する
    if (this.balance == null) {
      this.balance = BigDecimal.ZERO;
    }
    val amountToSubtract = event.getAmount();
    if (amountToSubtract != null) {
      this.balance = this.balance.subtract(amountToSubtract);
    }
    this.updatedAt = event.getUpdatedAt();
    this.updatedBy = event.getUpdatedBy();
  }

  public void handle(DestinationBankAccountCreditedEvent event) {
    // 出金した場合は、残高を減算する
    if (this.balance == null) {
      this.balance = BigDecimal.ZERO;
    }
    val amountToAdd = event.getAmount();
    if (amountToAdd != null) {
      this.balance = this.balance.add(amountToAdd);
    }
    this.updatedAt = event.getUpdatedAt();
    this.updatedBy = event.getUpdatedBy();
  }

  @JsonIgnore
  @Override
  public boolean isNew() {
    return this.updatedAt == null;
  }
}
