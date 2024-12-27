package org.aston.depositservice.service.impl.unit;

import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.persistance.repository.ProductRepository;
import org.aston.depositservice.service.impl.DepositProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.aston.depositservice.configuration.ApplicationConstant.PAGE_NOT_FOUND;
import static org.aston.depositservice.data.DepositData.AMOUNT_MAX;
import static org.aston.depositservice.data.DepositData.AMOUNT_MIN;
import static org.aston.depositservice.data.DepositData.CURRENCY_NAME;
import static org.aston.depositservice.data.DepositData.PERCENT_RATE;
import static org.aston.depositservice.data.DepositData.PRODUCT_NAME;
import static org.aston.depositservice.data.DepositData.createDepositProductInfoResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepositProductServiceImplTest {

    private final UUID product_id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private String productUuid = product_id.toString();
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private DepositProductServiceImpl depositProductService;

    private DepositProductInfoResponseDto expectedDepositProductInfoResponseDto;

    @BeforeEach
    public void setUp() {

        expectedDepositProductInfoResponseDto = createDepositProductInfoResponseDto();
    }

    @Test
    public void givenValidRequest_whenGetAllProductsShortInfo_thenReturnAllShortInfoList() {
        List<AllProductsShortInfoResponseDto> expectedAllProductsShortInfoResponseDtoList = new ArrayList<>();
        expectedAllProductsShortInfoResponseDtoList.add(new AllProductsShortInfoResponseDto(
                product_id.toString(),
                PRODUCT_NAME,
                CURRENCY_NAME,
                AMOUNT_MIN,
                AMOUNT_MAX,
                365,
                3650,
                15,
                true,
                (short) 3,
                true,
                PERCENT_RATE));

        doReturn(expectedAllProductsShortInfoResponseDtoList).when(productRepository).findAllBy();
        List<AllProductsShortInfoResponseDto> result = depositProductService.getAllShortInfoList();

        assertThat(result).isEqualTo(expectedAllProductsShortInfoResponseDtoList);
        assertThat(result).hasSize(1);
    }

    @Test
    public void givenEmptyList_whenGetAllProductsShortInfo_thenThrowException() {
        String exceptionMessage = "Список не существует";

        doReturn(new ArrayList<>()).when(productRepository).findAllBy();

        var exception = assertThrows(DepositException.class, () -> depositProductService.getAllShortInfoList());
        assertThat(exception.getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    void getDepositProductInfoByDepositId_whenDepositIdIsCorrect_thenReturnExpectedDepositProductInfoResponseDto() {
        when(productRepository.findDepositProductInfoByProductId(product_id))
                .thenReturn(Optional.of(expectedDepositProductInfoResponseDto));

        DepositProductInfoResponseDto actualResponseDto = depositProductService
                .getDepositProductInfoByProductId(productUuid);

        assertNotNull(actualResponseDto);
        assertEquals(expectedDepositProductInfoResponseDto, actualResponseDto);
        verify(productRepository, times(1)).findDepositProductInfoByProductId(product_id);
    }

    @Test
    void givenValidDepositId_whenDepositDoesNotExist_thenThrowDepositException() {
        when(productRepository.findDepositProductInfoByProductId(product_id)).thenReturn(Optional.empty());

        DepositException exception = assertThrows(DepositException.class, () -> {
            depositProductService.getDepositProductInfoByProductId(productUuid);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(PAGE_NOT_FOUND, exception.getMessage());
    }
}
