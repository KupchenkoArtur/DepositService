package org.aston.depositservice.persistance.repository;

import org.aston.depositservice.persistance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {}
