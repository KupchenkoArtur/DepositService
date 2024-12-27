package org.aston.depositservice.persistance.repository;

import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.ActiveDepositResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.persistance.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью Deposit.
 */
public interface DepositRepository extends JpaRepository<Deposit, UUID> {

    /**
     * Находит все действующие депозиты.
     *
     * @return Список действующих депозитов.
     */
    @Query("""
            SELECT new org.aston.depositservice.dto.response.ActiveDepositListResponseDto(
                cast(p.id AS string),
                p.name,
                p.currency,
                t.curBalance,
                cast(d.endDate AS string),
                da.mainNum,
                cast(d.id AS string)
            )
            FROM Deposit d
            LEFT JOIN d.product p
            LEFT JOIN d.transactions t
            LEFT JOIN d.account da
            WHERE d.customerId = :customerId AND d.depositStatus = :status
            """)
    List<ActiveDepositListResponseDto> findALLActiveDepositByCustomerId(UUID customerId, Boolean status);

    /**
     * Находит подробную информацию по депозиту
     *
     * @param customerId идентификатор клиента
     * @param depositId  идентификатор депозита
     * @return Подробная информация по депозиту
     */
    @Query("""
                SELECT new org.aston.depositservice.dto.response.ActiveDepositResponseDto(
                        p.name,
                        p.currency,
                        p.timeLimited,
                        p.revocable,
                        p.capitalization,
                        p.withdrawal,
                        p.productStatus,
                        p.autorenStatus,
                        d.initialAmount,
                        t.curBalance,
                        t.percBalance,
                        cast(d.startDate AS string),
                        cast(d.endDate AS string),
                        d.autorenStatus,
                        pr.percentRate,
                        da.mainNum,
                        da.percNum,
                        cast(da.mAccountId AS string),
                        cast(da.pAccountId AS string)
                    )
                FROM Deposit d
                LEFT JOIN d.product p
                LEFT JOIN d.account da
                LEFT JOIN da.transactions t
                LEFT JOIN p.percents pr
                WHERE d.id = :depositId AND d.depositStatus = true AND d.customerId = :customerId
            """)
    ActiveDepositResponseDto findActiveDepositByDepositId(@Param("depositId") UUID depositId, UUID customerId);

    @Query("""
            SELECT d
            FROM Deposit d
            LEFT JOIN d.product p ON p.capitalization = 0
            LEFT JOIN d.transactions t ON t.operDate <> CURRENT_DATE
            LEFT JOIN d.account da
            GROUP BY d.id, d.curPercent, t.curBalance,da.id
            HAVING t.operDate = MAX(t.operDate)
            """)
    List<Deposit> findActiveDepositsWithoutAccruedPercent();

    @Query("""
            SELECT d.id, d
            FROM Deposit d
            LEFT JOIN Transaction t on t.operType = 21 and t.operDate between :startDayTime and :endDayTime
            WHERE d.depositStatus = TRUE AND d.autorenStatus = FALSE AND d.closeDate = CURRENT_DATE
            """)
    Map<UUID, Deposit> getReadyToCloseDeposit(@Param("startDayTime") Instant startDayTime,
                                              @Param("endDayTime") Instant endDayTime);

    /**
     * Находит продукт по id депозита
     *
     * @param depositId идентификатор депозита
     * @return Optional<DepositProductInfoResponseDto> тело с выходными параметрами, содержащими информацию о продукте
     */
    @Query("""
                SELECT new org.aston.depositservice.dto.response.DepositProductInfoResponseDto(
                        cast(p.id AS string),
                        p.name,
                        p.currency,
                        p.amountMin,
                        p.amountMax,
                        p.productStatus,
                        p.autorenStatus,
                        p.dayMax,
                        p.dayMin,
                        p.timeLimited,
                        p.capitalization,
                        p.replenishment,
                        p.withdrawal,
                        p.revocable,
                        p.penalty,
                        pr.percentRate
                    )
                FROM Deposit d
                LEFT JOIN d.product p
                LEFT JOIN Percent pr ON pr.product = p
                WHERE d.id = :depositId
            """)
    Optional<DepositProductInfoResponseDto> findDepositProductInfoByDepositId(@Param("depositId") UUID depositId);

    Optional<Deposit> findByIdAndCustomerId(UUID depositId, UUID customerId);

    Optional<Deposit> findByIdAndCustomerIdAndDepositStatus(UUID depositId, UUID customerId, boolean depositStatus);
}
