package org.aston.depositservice.service;



import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;

import java.util.List;

/**
 * Сервис для работы с депозитыми продуктами.
 * Этот интерфейс предоставляет методы для поиска депозитных продуктов.
 */
public interface DepositProductService {

    /**
     * Получить список c краткой информацией о предлагаемых банком видах депозитных продуктов.
     *
     * @return Список депозитных продуктов.
     */
    List<AllProductsShortInfoResponseDto> getAllShortInfoList();

    /**
     * Получение подробной информации по депозитному продукту
     *
     * @param depositId id депозита, по которому необходимо найти информацию о продукте
     * @return возвращает полную информацию по продукту
     */
    DepositProductInfoResponseDto getDepositProductInfoByProductId(String productId);
}
