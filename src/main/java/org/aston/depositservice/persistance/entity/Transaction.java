package org.aston.depositservice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private DepositAccount account;

    @NotNull
    @Column(name = "oper_date", nullable = false)
    private Instant operDate;

    @NotNull
    @Column(name = "oper_type", nullable = false)
    private Short operType;

    @NotNull
    @Column(name = "oper_sum", nullable = false, precision = 12, scale = 2)
    private BigDecimal operSum;

    @NotNull
    @Column(name = "db_kt", nullable = false)
    private Short dbKt;

    @NotNull
    @Column(name = "perc_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal percBalance;

    @NotNull
    @Column(name = "cur_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal curBalance;

    @NotNull
    @Column(name = "total_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id")
    private Deposit deposit;
}