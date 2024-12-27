package org.aston.depositservice.controller.impl;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.aston.depositservice.controller.DepositProductController;

import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.service.DepositProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_DATA;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/deposit/deposit_product")
public class DepositProductControllerImpl implements DepositProductController {

    private final DepositProductService depositProductService;

    @Override
    @GetMapping("/all_products")
    public ResponseEntity<List<AllProductsShortInfoResponseDto>> getAllProductsShortInfoList() {
        return ResponseEntity.ok(depositProductService.getAllShortInfoList());
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<DepositProductInfoResponseDto> getDepositProductInfoByDepositId(
            @PathVariable("productId")
            @Pattern(regexp = REGEXP_UUID, message = INVALID_DATA) String productId) {
        return ResponseEntity.ok(depositProductService.getDepositProductInfoByProductId(productId));
    }
}
