package org.aston.depositservice.persistance.repository;

import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.persistance.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью DepositProduct.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Находит краткую информацию для всех депозитных продуктов банка.
     *
     * @return Список депозитных продуктов в кратком описании.
     */
    @Query("""
                SELECT new org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto(
                        cast(p.id AS java.lang.String),
                        p.name,
                        p.currency,
                        p.amountMin,
                        p.amountMax,
                        p.dayMin,
                        p.dayMax,
                        p.capitalization,
                        p.replenishment,
                        p.withdrawal,
                        p.revocable,
                        pr.percentRate
                    )
                FROM Product p
                LEFT JOIN Percent pr ON pr.product = p
            """)
    List<AllProductsShortInfoResponseDto> findAllBy();

    /**
     * Находит информацию о продукте по его id
     *
     * @param productId идентификатор продукта
     * @return Optional<DepositProductInfoResponseDto> тело с выходными параметрами, содержащими информацию о продукте
     */
    @Query("""
                SELECT new org.aston.depositservice.dto.response.DepositProductInfoResponseDto(
                        cast(p.id AS java.lang.String),
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
                FROM Product p
                LEFT JOIN Percent pr ON pr.product = p
                WHERE p.id = :productId
            """)
    Optional<DepositProductInfoResponseDto> findDepositProductInfoByProductId(@Param("productId") UUID productId);
}
