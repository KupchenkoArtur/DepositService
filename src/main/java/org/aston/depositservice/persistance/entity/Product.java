package org.aston.depositservice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 30)
    @NotNull
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Size(max = 3)
    @NotNull
    @ColumnDefault("'RUB'::bpchar")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @Column(name = "amount_min", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountMin;

    @NotNull
    @Column(name = "amount_max", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountMax;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "product_status", nullable = false)
    private Boolean productStatus;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "autoren_status", nullable = false)
    private Boolean autorenStatus;

    @Column(name = "day_max")
    private Integer dayMax;

    @Column(name = "day_min")
    private Integer dayMin;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "time_limited", nullable = false)
    private Boolean timeLimited;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "capitalization", nullable = false)
    private Integer capitalization;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "replenishment", nullable = false)
    private Boolean replenishment;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "withdrawal", nullable = false)
    private Short withdrawal;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "revocable", nullable = false)
    private Boolean revocable;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "penalty", nullable = false, precision = 3, scale = 2)
    private BigDecimal penalty;

    @OneToMany(mappedBy = "product")
    private Set<Deposit> deposits;

    @OneToMany(mappedBy = "product")
    private Set<Percent> percents;
}