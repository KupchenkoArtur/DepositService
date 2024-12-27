package org.aston.depositservice.service.impl.unit;

import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.mapper.TransactionMapper;
import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.Product;
import org.aston.depositservice.persistance.entity.Transaction;
import org.aston.depositservice.persistance.repository.DepositRepository;
import org.aston.depositservice.persistance.repository.TransactionRepository;
import org.aston.depositservice.service.impl.SchedulerTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerTasksTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private SchedulerTasks schedulerTasks;

    private List<Deposit> deposits;

    @BeforeEach
    void setUp() {
        deposits = new ArrayList<>();

        Deposit deposit = new Deposit();
        Product product = new Product();
        product.setCapitalization(0);
        deposit.setId(UUID.randomUUID());
        deposit.setCurPercent(BigDecimal.valueOf(0.05));
        deposit.setProduct(product);

        Transaction transaction = new Transaction();
        transaction.setCurBalance(BigDecimal.valueOf(1000));
        transaction.setPercBalance(BigDecimal.valueOf(10));
        transaction.setOperDate(LocalDate.now().minusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        deposit.setTransactions(Set.of(transaction));
        deposits.add(deposit);
    }

    @Test
    void testCalculateSimplePercentSuccessAndTransactionIsSave() {
        when(depositRepository.findActiveDepositsWithoutAccruedPercent()).thenReturn(deposits);

        Transaction transaction = new Transaction();
        when(transactionMapper.mapDepositToTransaction(any(Deposit.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(transaction);

        schedulerTasks.calculateSimplePercent();

        verify(depositRepository, times(1)).findActiveDepositsWithoutAccruedPercent();
        verify(transactionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCalculateSimplePercentNoActiveDeposits() {
        when(depositRepository.findActiveDepositsWithoutAccruedPercent()).thenReturn(new ArrayList<>());

        assertThrows(DepositException.class, () -> schedulerTasks.calculateSimplePercent());

        verify(depositRepository, times(1)).findActiveDepositsWithoutAccruedPercent();
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCalculateSimplePercentNoTransactions() {
        deposits.get(0).setTransactions(Set.of());

        when(depositRepository.findActiveDepositsWithoutAccruedPercent()).thenReturn(deposits);

        assertThrows(DepositException.class, () -> schedulerTasks.calculateSimplePercent());

        verify(depositRepository, times(1)).findActiveDepositsWithoutAccruedPercent();
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCalculateCompoundPercentNoActiveDeposits() {
        when(depositRepository.findActiveDepositsWithoutAccruedPercent()).thenReturn(new ArrayList<>());

        assertThrows(DepositException.class, () -> schedulerTasks.calculateSimplePercent());

        verify(depositRepository, times(1)).findActiveDepositsWithoutAccruedPercent();
        verify(transactionRepository, never()).saveAll(any(List.class));
    }

    @Test
    void testCalculateCompoundPercentNoTransactions() {
        deposits.get(0).setTransactions(Set.of());

        when(depositRepository.findActiveDepositsWithoutAccruedPercent()).thenReturn(deposits);

        assertThrows(DepositException.class, () -> schedulerTasks.calculateSimplePercent());

        verify(depositRepository, times(1)).findActiveDepositsWithoutAccruedPercent();
        verify(transactionRepository, never()).saveAll(any(List.class));
    }
}
