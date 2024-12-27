package org.aston.depositservice.controller.impl.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aston.depositservice.controller.impl.DepositsControllerImpl;
import org.aston.depositservice.dto.request.AutorenewalRequestDto;
import org.aston.depositservice.dto.request.CloseDepositRequestDto;
import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.CloseDepositResponseDto;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.service.impl.DepositServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.DATA_NOT_FOUND_MESSAGE;
import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_ID;
import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_STATUS;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_DATA;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_UUID;
import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_REQUEST_HEADERS;
import static org.aston.depositservice.configuration.ApplicationConstant.UNSUPPORTED_DATA_TYPE;
import static org.aston.depositservice.data.DepositData.CUSTOMER_ID;
import static org.aston.depositservice.data.DepositData.createActiveDepositListResponseDto;
import static org.aston.depositservice.data.DepositData.createNewDepositRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepositsControllerImpl.class)
@ExtendWith(MockitoExtension.class)
class DepositsControllerImplTest {

    private final String depositId = "123e4567-e89b-12d3-a456-426614174593";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DepositServiceImpl depositService;
    private List<ActiveDepositListResponseDto> expectedActiveDepositListResponseDtoList;

    @BeforeEach
    void setUp() {
        expectedActiveDepositListResponseDtoList = new ArrayList<>();
        expectedActiveDepositListResponseDtoList.add(createActiveDepositListResponseDto());
    }

    private NewDepositRequestDto newDepositRequestDto;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String URL = "/api/v1/deposit/deposits";

    @Test
    void givenValidCustomerId_getActiveDepositList_thenReturnsActiveDepositListResponseDto() throws Exception {
        doReturn(expectedActiveDepositListResponseDtoList).when(depositService).getActiveDepositList(CUSTOMER_ID);

        mockMvc.perform(get(URL)
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID))
                .andExpect(content().string(mapper.writeValueAsString(expectedActiveDepositListResponseDtoList)))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidCustomerIdWhoseMissingDeposit_getActiveDepositList_thenReturnsNotFoundRequest() throws Exception {
        doThrow(new DepositException("Действующие депозиты отсутсвуют")).when(depositService).getActiveDepositList(CUSTOMER_ID);

        mockMvc.perform(get(URL)
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID))
                .andExpect(content().string("Действующие депозиты отсутсвуют"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenDepositIdAndStatus_changeAutorenewalStatus_shouldReturnsOk() throws Exception {
        AutorenewalRequestDto dto = new AutorenewalRequestDto(true);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + depositId + "/autoRenewal")
                .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
    }

    @Test
    void givenDepositIdAndInvalidStatus_changeAutorenewalStatus_shouldReturnsBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + depositId + "/autoRenewal")
                .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                .content(mapper.writeValueAsString("tru"))
                .contentType(APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(INVALID_DATA, actualResponseMessage);
    }

    @Test
    void closeDeposit_shouldReturnOk() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(false);

        CloseDepositResponseDto responseDto = new CloseDepositResponseDto();
        responseDto.setProductName("123");
        responseDto.setDepositStatus(false);
        responseDto.setAccountId(CUSTOMER_ID);
        responseDto.setClosedDate(Instant.now().toString());

        when(depositService.closeDeposit(eq(depositId), any(CloseDepositRequestDto.class), eq(CUSTOMER_ID)))
                .thenReturn(responseDto);

        mockMvc.perform(patch(URL + "/" + depositId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(responseDto)));

    }

    @Test
    void closeDeposit_shouldReturnNotFound() throws Exception {
        doThrow(new DepositException(HttpStatus.NOT_FOUND, DATA_NOT_FOUND_MESSAGE))
                .when(depositService).closeDeposit(eq(depositId), Mockito.any(CloseDepositRequestDto.class), eq(CUSTOMER_ID));

        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(false);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + depositId)
                .content(mapper.writeValueAsString(dto))
                .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                .contentType(APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(DATA_NOT_FOUND_MESSAGE, actualResponseMessage);
    }

    @Test
    void givenInvalidContentType_whenCloseDeposit_shouldReturnUnsupportedMediaType() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(false);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + depositId)
                .content(mapper.writeValueAsString(dto))
                .contentType("text/plain");

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isUnsupportedMediaType())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(UNSUPPORTED_DATA_TYPE, actualResponseMessage);
    }

    @Test
    void givenMissingDepositId_whenCloseDeposit_shouldReturnBadRequest() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(false);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/ ")
                .content(mapper.writeValueAsString(dto))
                .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                .contentType(APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(INVALID_DATA, actualResponseMessage);
    }

    @Test
    void givenMissingRequestHeaders_whenCloseDeposit_shouldReturnBadRequest() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(false);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + depositId)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(MISSING_REQUEST_HEADERS, actualResponseMessage);
    }

    @Test
    void givenMissingRequestBody_whenCloseDeposit_shouldReturnBadRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + depositId)
                .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(INVALID_DATA, actualResponseMessage);
    }

    @Test
    void givenInvalidDepositIdFormat_whenCloseDeposit_shouldReturnBadRequest() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(false);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(URL + "/" + INVALID_UUID)
                .content(mapper.writeValueAsString(dto))
                .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                .contentType(APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(INVALID_DATA, actualResponseMessage);
    }

    @Test
    void givenInvalidDepositStatus_whenCloseDeposit_shouldReturnBadRequest() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(true);

        doThrow(new DepositException(HttpStatus.BAD_REQUEST, INVALID_DATA))
                .when(depositService).closeDeposit(eq(depositId), any(CloseDepositRequestDto.class), eq(CUSTOMER_ID));

        mockMvc.perform(patch(URL + "/" + depositId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void givenEmptyDepositStatus_whenCloseDeposit_shouldReturnBadRequest() throws Exception {
        CloseDepositRequestDto dto = new CloseDepositRequestDto();
        dto.setDepositStatus(true);

        String jsonRequest = "{ \"depositStatus\": \" \" }";

        MvcResult result = mockMvc.perform(patch(URL + "/" + depositId)
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID))
                .andExpect(status().isBadRequest())
                .andReturn();


        String actualResponseMessage = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals("{\"depositStatus\":\"Отсутствует параметр\"}", actualResponseMessage);
    }

    @Test
    void createDeposit_whenDataDoesntCorrect_thenReturnForbiddenCode() throws Exception {
        newDepositRequestDto = createNewDepositRequestDto();

        doThrow(new DepositException(FORBIDDEN, "Ошибка доступа")).when(depositService).createDeposit(any(), any(), any());

        mockMvc.perform(post(URL)
                        .header(HEADER_KEY_CUSTOMER_ID, CUSTOMER_ID)
                        .header(HEADER_KEY_CUSTOMER_STATUS, "0")
                        .content(mapper.writeValueAsString(newDepositRequestDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}