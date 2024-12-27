package org.aston.depositservice.controller.impl.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aston.depositservice.configuration.ApplicationConstant;
import org.aston.depositservice.controller.impl.DepositControllerImpl;
import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.ActiveDepositResponseDto;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.service.impl.DepositServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.DATA_NOT_FOUND_MESSAGE;
import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_ID;
import static org.aston.depositservice.data.DepositData.*;
import static org.aston.depositservice.data.DepositData.createActiveDepositResponseDto;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepositControllerImpl.class)
@ExtendWith(MockitoExtension.class)
class DepositControllerImplTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DepositServiceImpl depositService;
    private List<ActiveDepositListResponseDto> expectedActiveDepositListResponseDtoList;
    private ActiveDepositResponseDto expectedActiveDepositResponseDto;
    private final ObjectMapper mapper = new ObjectMapper();
    @BeforeEach
    void setUp() {
        expectedActiveDepositListResponseDtoList = new ArrayList<>();
        expectedActiveDepositListResponseDtoList.add(createActiveDepositListResponseDto());
        expectedActiveDepositResponseDto = createActiveDepositResponseDto();
    }

    @Test
    void getActiveCustomerDeposit_shouldThrowDepositException_whenParamsIsInvalid() throws Exception {
        doThrow(new DepositException(BAD_REQUEST, DATA_NOT_FOUND_MESSAGE))
                .when(depositService).getActiveCustomerDeposit(CUSTOMER_ID, TEST_DEPOSIT_ID);

        mockMvc.perform(get(DEPOSIT_CONTROLLER_URL + "/info")
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                        .param(ApplicationConstant.DEPOSIT_ID, TEST_DEPOSIT_ID))
                .andExpect(content().string(DATA_NOT_FOUND_MESSAGE))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getActiveCustomerDeposit_shouldReturnOkStatus_whenParamsIsValid() throws Exception {
        doReturn(expectedActiveDepositResponseDto).when(depositService).getActiveCustomerDeposit(CUSTOMER_ID, TEST_DEPOSIT_ID);

        mockMvc.perform(get(DEPOSIT_CONTROLLER_URL + "/info")
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                        .param(ApplicationConstant.DEPOSIT_ID, TEST_DEPOSIT_ID))
                .andExpect(content().json(mapper.writeValueAsString(expectedActiveDepositResponseDto)))
                .andExpect(status().isOk());
    }
}
