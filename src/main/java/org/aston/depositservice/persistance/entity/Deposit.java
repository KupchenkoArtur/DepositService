package org.aston.depositservice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "deposit")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private DepositAccount account;

    @NotNull
    @Column(name = "initial_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal initialAmount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(name = "close_date")
    private Instant closeDate;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "deposit_status", nullable = false)
    private Boolean depositStatus;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "autoren_status", nullable = false)
    private Boolean autorenStatus;

    @NotNull
    @Column(name = "cur_percent", nullable = false, precision = 4, scale = 2)
    private BigDecimal curPercent;

    @OneToMany(mappedBy = "deposit")
    private Set<Transaction> transactions;
}