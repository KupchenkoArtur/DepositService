package org.aston.depositservice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "deposit_account")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositAccount {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 20)
    @Column(name = "main_num")
    private String mainNum;

    @Size(max = 20)
    @Column(name = "perc_num")
    private String percNum;

    @NotNull
    @Column(name = "m_account_id", nullable = false)
    private UUID mAccountId;

    @Column(name = "p_account_id")
    private UUID pAccountId;

    @OneToOne(mappedBy = "account")
    private Deposit deposit;

    @OneToMany(mappedBy = "account")
    private Set<Transaction> transactions;
}