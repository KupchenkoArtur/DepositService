package org.aston.depositservice.persistance.repository;

import org.aston.depositservice.persistance.entity.DepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью DepositAccount.
 */
@Repository
public interface DepositAccountRepository extends JpaRepository<DepositAccount, UUID> {
}
