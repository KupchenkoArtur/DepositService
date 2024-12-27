package org.aston.depositservice.controller.impl.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aston.depositservice.controller.impl.DepositProductControllerImpl;
import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.service.impl.DepositProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.DEPOSIT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_DATA;
import static org.aston.depositservice.data.DepositData.AMOUNT_MAX;
import static org.aston.depositservice.data.DepositData.AMOUNT_MIN;
import static org.aston.depositservice.data.DepositData.CURRENCY_NAME;
import static org.aston.depositservice.data.DepositData.PERCENT_RATE;
import static org.aston.depositservice.data.DepositData.PRODUCT_NAME;
import static org.aston.depositservice.data.DepositData.TEST_PRODUCT_ID;
import static org.aston.depositservice.data.DepositData.createDepositProductInfoResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepositProductControllerImpl.class)
@ExtendWith(MockitoExtension.class)
public class DepositProductControllerImplTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DepositProductServiceImpl depositProductService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String URL = "/api/v1/deposit/deposit_product";

    private final String product_id = "123e4567-e89b-12d3-a456-426614174001";

    private final String WRONG_PRODUCT_ID = "f3a2c3d4-e5f6-4890-abcd-ef1234567890354675896754w3";

    private List<AllProductsShortInfoResponseDto> expectedAllShortInfoDtoList;

    private DepositProductInfoResponseDto expectedDepositProductInfoResponseDto;

    @BeforeEach
    public void setUp() {
        expectedAllShortInfoDtoList = new ArrayList<>();
        expectedAllShortInfoDtoList.add(new AllProductsShortInfoResponseDto(
                product_id,
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

        expectedDepositProductInfoResponseDto = createDepositProductInfoResponseDto();
    }

    @Test
    public void givenValidRequest_whenGetAllShortInfo_thenReturnAllProductsShortInfoList() throws Exception {
        doReturn(expectedAllShortInfoDtoList).when(depositProductService).getAllShortInfoList();

        mockMvc.perform(get(URL + "/all_products"))
                .andExpect(content().string(mapper.writeValueAsString(expectedAllShortInfoDtoList)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenValidRequestButListIsEmpty_whenGetAllShortInfo_thenThrowException() throws Exception {
        String exceptionMessage = "Список не существует";

        doThrow(new DepositException(exceptionMessage)).when(depositProductService).getAllShortInfoList();

        mockMvc.perform(get(URL + "/all_products"))
                .andExpect(content().string(exceptionMessage))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidDepositId_whenGetDepositProductInfo_thenReturnsOk() throws Exception {
        when(depositProductService.getDepositProductInfoByProductId(TEST_PRODUCT_ID)).thenReturn(expectedDepositProductInfoResponseDto);

        mockMvc.perform(get(URL + "/{productId}", TEST_PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedDepositProductInfoResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedDepositProductInfoResponseDto.getName()));

        verify(depositProductService, times(1)).getDepositProductInfoByProductId(TEST_PRODUCT_ID);
    }

    @Test
    public void givenInvalidDepositId_whenRequestIsMade_thenReturnsBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URL + "/{productId}", WRONG_PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(actualResponseMessage.contains(INVALID_DATA));
    }

    @Test
    public void givenValidDepositId_whenDepositIsNotFound_thenReturnsNotFound() throws Exception {
        doThrow(new DepositException(HttpStatus.NOT_FOUND, DEPOSIT_NOT_FOUND))
                .when(depositProductService).getDepositProductInfoByProductId(anyString());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URL + "/{productId}", TEST_PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(DEPOSIT_NOT_FOUND, actualResponseMessage);
    }

    @Test
    public void givenEmptyDepositId_whenRequestIsMade_thenReturnsBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URL + "/{productId}", "  ")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(actualResponseMessage.contains(INVALID_DATA));
    }
}
