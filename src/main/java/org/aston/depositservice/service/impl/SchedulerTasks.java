package org.aston.depositservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.exception.ScheduleTaskException;
import org.aston.depositservice.mapper.TransactionMapper;
import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.Transaction;
import org.aston.depositservice.persistance.repository.DepositRepository;
import org.aston.depositservice.persistance.repository.TransactionRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.EXCEEDED_NUMBER_ATTEMPTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerTasks {

    private final DepositRepository depositRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private static final int MAX_DB_ACCESS_ERRORS = 4;
    private static final String SIMPLE = "простых";
    private static final String COMPOUND = "сложных";
    private static final String ACCRUAL_MESSAGE = "Начисление {} процентов для депозита с id: {}";

    @Transactional
    public void calculatePercent() throws ScheduleTaskException {
        int dbAccessAttemptsCount = 1;
        while (dbAccessAttemptsCount <= MAX_DB_ACCESS_ERRORS) {
            try {
                List<Deposit> deposits = depositRepository.findActiveDepositsWithoutAccruedPercent();
                if (deposits.isEmpty()) {
                    log.error("Нет активных депозитов для начисления процентов");
                    throw new DepositException("Депозиты отсутствуют");
                }
                log.info("Процесс начисления процентов начался");
                List<Transaction> transactionsToSave = deposits.stream().map(this::processDepositInterest).toList();
                transactionRepository.saveAll(transactionsToSave);
                break;
            } catch (DataAccessException dataAccessException) {
                log.error("Ошибка доступа к базе данных (попытка {})", dbAccessAttemptsCount, dataAccessException);
            }
            dbAccessAttemptsCount++;
        }
        if (dbAccessAttemptsCount > MAX_DB_ACCESS_ERRORS) {
            throw new ScheduleTaskException(EXCEEDED_NUMBER_ATTEMPTS);
        }
    }

    public void calculateSimplePercent() {
        try {
            calculatePercent();
        } catch (ScheduleTaskException e) {
            log.info(EXCEEDED_NUMBER_ATTEMPTS + ": {}", MAX_DB_ACCESS_ERRORS);
        }
    }

    private int getDaysInYear() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.isLeapYear() ? 366 : 365;
    }

    private Transaction getLastTransaction(Deposit deposit) {
        return deposit.getTransactions()
                .stream()
                .max(Comparator.comparing(Transaction::getOperDate))
                .orElseThrow(() -> new DepositException("На депозите нет входящих транзакций."));
    }

    /**
     * Рассчет сложных процентов по депозиту
     *
     * @param balance  Сумма депозита для расчета
     * @param yearRate Годовая процентная ставка
     * @return Список депозитных продуктов.
     */
    public BigDecimal calculateBalance(BigDecimal balance, BigDecimal yearRate) {
        MathContext mc = new MathContext(100, RoundingMode.HALF_UP);
        BigDecimal daysInYear = new BigDecimal(getDaysInYear());
        BigDecimal dailyRate = yearRate.divide(daysInYear, mc).divide(BigDecimal.valueOf(100), mc);
        return balance.multiply(dailyRate, mc);
    }

    private Transaction processDepositInterest(Deposit deposit) {
        Transaction lastTransaction = getLastTransaction(deposit);
        BigDecimal percent;
        boolean isSimplePercent = deposit.getProduct().getCapitalization() == 0;

        if (isSimplePercent) {
            log.info(ACCRUAL_MESSAGE, SIMPLE, deposit.getId());
            percent = calculateBalance(lastTransaction.getCurBalance(), deposit.getCurPercent());
        } else {
            log.info(ACCRUAL_MESSAGE, COMPOUND, deposit.getId());
            percent = calculateBalance(lastTransaction.getTotalBalance(), deposit.getCurPercent());
        }

        BigDecimal updatedPercBalance = lastTransaction.getPercBalance().add(percent);
        Transaction transaction = transactionMapper.mapDepositToTransaction(deposit, percent,
                lastTransaction.getCurBalance(), updatedPercBalance);

        log.info("Начисление {} процентов для депозита с id: {}, успешно завершено",
                isSimplePercent ? SIMPLE : COMPOUND, deposit.getId());
        return transaction;
    }
}
