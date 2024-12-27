package org.aston.depositservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.persistance.repository.ProductRepository;
import org.aston.depositservice.service.DepositProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.aston.depositservice.configuration.ApplicationConstant.PAGE_NOT_FOUND;

@Slf4j
@Service
@AllArgsConstructor
public class DepositProductServiceImpl implements DepositProductService {

    private final ProductRepository productRepository;

    @Override
    public List<AllProductsShortInfoResponseDto> getAllShortInfoList() {
        List<AllProductsShortInfoResponseDto> allProductsShortInfoResponseDtoList = productRepository.findAllBy();
        if (allProductsShortInfoResponseDtoList.isEmpty()) {
            throw new DepositException("Список не существует");
        }
        log.info("Успешно получен список с краткой информацией о депозитных продуктах");
        return productRepository.findAllBy();
    }

    @Override
    public DepositProductInfoResponseDto getDepositProductInfoByProductId(String productId) {
        UUID productUuid = UUID.fromString(productId);

        log.info("Поиск продукта с id: {}", productUuid);
        DepositProductInfoResponseDto depositProductInfoResponseDto = productRepository
                .findDepositProductInfoByProductId(productUuid)
                .orElseThrow(() -> new DepositException(HttpStatus.NOT_FOUND, PAGE_NOT_FOUND));

        log.info("Продукт c id {} успешно найден", depositProductInfoResponseDto.getId());

        return depositProductInfoResponseDto;
    }
}
