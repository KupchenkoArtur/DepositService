package org.aston.depositservice.service.impl.unit;

import org.aston.depositservice.configuration.ApplicationConstant;
import org.aston.depositservice.data.DepositData;
import org.aston.depositservice.dto.request.AutorenewalRequestDto;
import org.aston.depositservice.dto.request.CloseDepositRequestDto;
import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.request.ProlongDepositRequestDto;
import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.ActiveDepositResponseDto;
import org.aston.depositservice.dto.response.AutorenewalStatusResponseDto;
import org.aston.depositservice.dto.response.CloseDepositResponseDto;
import org.aston.depositservice.dto.response.NewDepositResponseDto;
import org.aston.depositservice.dto.response.ProlongDepositResponseDto;
import org.aston.depositservice.exception.DepositException;
import org.aston.depositservice.mapper.DepositMapper;
import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.DepositAccount;
import org.aston.depositservice.persistance.entity.Product;
import org.aston.depositservice.persistance.repository.DepositAccountRepository;
import org.aston.depositservice.persistance.repository.DepositRepository;
import org.aston.depositservice.persistance.repository.ProductRepository;
import org.aston.depositservice.service.impl.DepositServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.aston.depositservice.configuration.ApplicationConstant.ACTIVE_DEPOSIT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.DATA_NOT_FOUND_MESSAGE;
import static org.aston.depositservice.configuration.ApplicationConstant.DEPOSIT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.UPDATE_AUTORENEWAL_STATUS;
import static org.aston.depositservice.data.DepositData.CUSTOMER_ID;
import static org.aston.depositservice.data.DepositData.DEPOSIT_ALREADY_CLOSED;
import static org.aston.depositservice.data.DepositData.INCORRECT_TEST_DEPOSIT_ID;
import static org.aston.depositservice.data.DepositData.INVALID_DATA;
import static org.aston.depositservice.data.DepositData.TEST_DEPOSIT_ID;
import static org.aston.depositservice.data.DepositData.createActiveDepositListResponseDto;
import static org.aston.depositservice.data.DepositData.createActiveDepositResponseDto;
import static org.aston.depositservice.data.DepositData.createDeposit;
import static org.aston.depositservice.data.DepositData.createDepositAccount;
import static org.aston.depositservice.data.DepositData.createDepositProduct;
import static org.aston.depositservice.data.DepositData.createNewDepositRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class DepositServiceImplTest {
    @Mock
    private DepositRepository depositRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private DepositAccountRepository depositAccountRepository;
    @Mock
    private DepositMapper depositMapper;
    @InjectMocks
    private DepositServiceImpl depositService;

    private Deposit deposit;
    private Deposit savedDeposit;
    private UUID depositId;
    private String depositUuid;
    private ProlongDepositRequestDto request;
    private String customerIdWhoseMissingDeposit;
    private AutorenewalRequestDto autorenewalRequestDto;
    private AutorenewalStatusResponseDto expectedAutorenewalStatusResponseDto;
    private List<ActiveDepositListResponseDto> expectedActiveDepositListResponseDtoList;
    private ActiveDepositResponseDto expectedActiveDepositResponseDto;
    private NewDepositRequestDto newDepositRequestDto;

    @BeforeEach
    public void setUp() {
        depositId = UUID.randomUUID();
        depositUuid = depositId.toString();
        customerIdWhoseMissingDeposit = UUID.randomUUID().toString();
        request = new ProlongDepositRequestDto(depositId.toString(), 90);
        deposit = DepositData.createDeposit();
        savedDeposit = DepositData.createDeposit();
        LocalDateTime newClosedAt = deposit.getEndDate().atZone(ZoneOffset.UTC).toLocalDateTime().plus(90, ChronoUnit.DAYS);
        savedDeposit.setEndDate(newClosedAt.toInstant(ZoneOffset.UTC));
        autorenewalRequestDto = new AutorenewalRequestDto(true);
        expectedAutorenewalStatusResponseDto = new AutorenewalStatusResponseDto(UPDATE_AUTORENEWAL_STATUS, true);
        expectedActiveDepositListResponseDtoList = new ArrayList<>();
        expectedActiveDepositListResponseDtoList.add(createActiveDepositListResponseDto());
        expectedActiveDepositResponseDto = createActiveDepositResponseDto();
    }

    @Test
    void givenValidCustomerId_whenGetActiveDepositList_thenReturnActiveDepositListResponseDto() {
        doReturn(expectedActiveDepositListResponseDtoList).when(depositRepository).findALLActiveDepositByCustomerId(UUID.fromString(CUSTOMER_ID), true);
        List<ActiveDepositListResponseDto> result = depositService.getActiveDepositList(CUSTOMER_ID);

        assertThat(result)
                .isEqualTo(expectedActiveDepositListResponseDtoList)
                .hasSize(1);
    }

    @Test
    void givenValidCustomerIdWhoseMissingDeposit_whenGetActiveDepositList_thenReturnThrowNotFoundException() {
        doReturn(new ArrayList<>()).when(depositRepository).findALLActiveDepositByCustomerId(UUID.fromString(customerIdWhoseMissingDeposit), true);

        var exception = assertThrows(DepositException.class, () -> depositService.getActiveDepositList(customerIdWhoseMissingDeposit));
        assertThat(exception.getMessage()).isEqualTo(ACTIVE_DEPOSIT_NOT_FOUND);
    }

    @Test
    public void givenValidRequest_whenProlongDeposit_thenReturnProlongDepositResponseDto() {
        doReturn(Optional.of(deposit)).when(depositRepository).findById(depositId);
        doReturn(savedDeposit).when(depositRepository).save(any(Deposit.class));
        ProlongDepositResponseDto expectedResponse = new ProlongDepositResponseDto();
        doReturn(expectedResponse).when(depositMapper).depositToProlongDepositResponseDto(savedDeposit);

        ProlongDepositResponseDto response = depositService.prolongDeposit(request);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    public void givenInvalidDepositId_whenProlongDeposit_thenThrowException() {
        doReturn(Optional.empty()).when(depositRepository).findById(depositId);

        DepositException exception = assertThrows(DepositException.class, () -> depositService.prolongDeposit(request));
        assertThat(exception.getMessage()).isEqualTo(DEPOSIT_NOT_FOUND);
    }

    @Test
    void givenValidParams_whenGetActiveCustomerDeposit_thenReturnsActiveDepositResponseDto() {
        doReturn(expectedActiveDepositResponseDto).when(depositRepository).findActiveDepositByDepositId(UUID.fromString(TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID));
        ActiveDepositResponseDto result = depositService.getActiveCustomerDeposit(CUSTOMER_ID, TEST_DEPOSIT_ID);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedActiveDepositResponseDto);
    }

    @Test
    void givenValidCustomerIdAndIncorrectDepositId_whenGetActiveCustomerDeposit_thenReturnsThrowDepositException() {
        doReturn(null).when(depositRepository).findActiveDepositByDepositId(UUID.fromString(INCORRECT_TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID));
        var exception = assertThrows(DepositException.class, () -> depositService.getActiveCustomerDeposit(CUSTOMER_ID, INCORRECT_TEST_DEPOSIT_ID));

        assertThat(exception.getMessage()).isEqualTo(ApplicationConstant.DATA_NOT_FOUND_MESSAGE);
        assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
    }

    @Test
    void givenValidDepositId_whenChangeAutoAutorenewalStatus_thenReturnAutorenewalStatusResponseDto() {
        Deposit deposit = Deposit.builder().autorenStatus(false).build();
        doReturn(Optional.of(deposit)).when(depositRepository).findByIdAndCustomerIdAndDepositStatus(UUID.fromString(TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID), true);
        AutorenewalStatusResponseDto actualAutorenewalStatusResponseDto =
                depositService.changeAutorenewalStatus(TEST_DEPOSIT_ID, autorenewalRequestDto, CUSTOMER_ID);

        assertThat(actualAutorenewalStatusResponseDto)
                .isNotNull()
                .isEqualTo(expectedAutorenewalStatusResponseDto);
    }

    @Test
    void givenNotValidDepositId_whenChangeAutoAutorenewalStatus_thenReturnNotFound() {
        assertThrows(DepositException.class, () -> depositService.changeAutorenewalStatus(TEST_DEPOSIT_ID, autorenewalRequestDto, CUSTOMER_ID));
    }

    @Test
    void givenValidDepositIdButStatusAlreadySet_whenChangeAutoAutorenewalStatus_thenReturnBadRequest() {
        autorenewalRequestDto.setAutorenewal(false);
        Deposit deposit = new Deposit();
        deposit.setAutorenStatus(false);
        assertThrows(DepositException.class, () -> depositService.changeAutorenewalStatus(TEST_DEPOSIT_ID, autorenewalRequestDto, CUSTOMER_ID));
    }

    @Test
    void createDeposit_whenProductDoesntExists_thenReturnProductNotFound() {
        MockitoAnnotations.openMocks(this);

        newDepositRequestDto = createNewDepositRequestDto();

        assertThrows(DepositException.class, () -> depositService.createDeposit(newDepositRequestDto, "1", CUSTOMER_ID));
    }

    @Test
    void createDeposit_whenDataIsCorrect_thenReturnSuccessCode() {
        NewDepositRequestDto newDepositRequestDto = createNewDepositRequestDto();
        Product depositProduct = createDepositProduct();
        Deposit deposit = createDeposit();
        DepositAccount depositAccount = createDepositAccount();

        when(productRepository.findById(any())).thenReturn(Optional.of(depositProduct));

        when(depositMapper.dtoToDeposit(newDepositRequestDto, depositProduct, depositAccount,
                true, true, CUSTOMER_ID, depositProduct.getAmountMin())).thenReturn(deposit);
        when(depositAccountRepository.findById(UUID.fromString(newDepositRequestDto.getAccountId()))).thenReturn(Optional.of(depositAccount));
        when(depositRepository.save(any(Deposit.class))).thenReturn(deposit);
        when(depositMapper.depositToDto(deposit, depositProduct.getName())).thenReturn(new NewDepositResponseDto());

        NewDepositResponseDto responseDto = depositService.createDeposit(newDepositRequestDto, "1",
                CUSTOMER_ID);

        assertNotNull(responseDto);
        verify(productRepository, times(1)).findById(any());
        verify(depositMapper, times(1)).dtoToDeposit(newDepositRequestDto, depositProduct, depositAccount,
                true, true, CUSTOMER_ID, depositProduct.getAmountMin());
        verify(depositRepository, times(1)).save(any(Deposit.class));
        verify(depositMapper, times(1)).depositToDto(deposit, depositProduct.getName());
    }

    @Test
    void testAutoCloseDeposit() {
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        Map<UUID, Deposit> depositMap = new HashMap<>();
        UUID depositId = UUID.randomUUID();
        Deposit deposit = new Deposit();
        deposit.setId(depositId);
        deposit.setDepositStatus(true);
        depositMap.put(depositId, deposit);

        when(depositRepository.getReadyToCloseDeposit(startOfDay, endOfDay)).thenReturn(depositMap);

        depositService.autoCloseDeposit();

        verify(depositRepository).getReadyToCloseDeposit(startOfDay, endOfDay);
        verify(depositRepository).saveAll(depositMap.values());

        ArgumentCaptor<Iterable<Deposit>> depositCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(depositRepository).saveAll(depositCaptor.capture());

        Iterable<Deposit> savedDeposits = depositCaptor.getValue();
        for (Deposit savedDeposit : savedDeposits) {
            assertFalse(savedDeposit.getDepositStatus());
        }
    }

    @Test
    void givenValidDepositIdAndOpenStatus_whenCloseDeposit_thenReturnCloseDepositResponseDto() {
        Deposit deposit = createDeposit();
        UUID testDepositId = deposit.getId();

        CloseDepositRequestDto closeRequestDto = new CloseDepositRequestDto();
        closeRequestDto.setDepositStatus(false);

        CloseDepositResponseDto expectedResponseDto = new CloseDepositResponseDto();
        expectedResponseDto.setProductName(deposit.getProduct().getName());
        expectedResponseDto.setDepositStatus(false);
        expectedResponseDto.setAccountId(deposit.getAccount().getMainNum());

        when(depositRepository.findByIdAndCustomerId(testDepositId, UUID.fromString(CUSTOMER_ID))).thenReturn(Optional.of(deposit));
        when(depositMapper.depositToCloseDepositResponseDto(deposit)).thenReturn(expectedResponseDto);

        CloseDepositResponseDto response = depositService.closeDeposit(testDepositId.toString(), closeRequestDto, CUSTOMER_ID);
        expectedResponseDto.setClosedDate(deposit.getCloseDate().toString());

        verify(depositRepository, times(1)).findByIdAndCustomerId(testDepositId, UUID.fromString(CUSTOMER_ID));
        verify(depositMapper, times(1)).depositToCloseDepositResponseDto(deposit);

        assertNotNull(response);
        assertEquals(expectedResponseDto, response);
        assertEquals(deposit.getProduct().getName(), response.getProductName());
        assertEquals(deposit.getAccount().getMainNum(), response.getAccountId());
        assertEquals(deposit.getDepositStatus(), response.isDepositStatus());
        assertEquals(deposit.getCloseDate().toString(), response.getClosedDate());
    }

    @Test
    void givenDepositAlreadyClosed_whenCloseDeposit_thenThrowDepositException() {
        Deposit testDeposit = createDeposit();
        testDeposit.setDepositStatus(false);
        testDeposit.setCloseDate(Instant.now());

        CloseDepositRequestDto closeRequestDto = new CloseDepositRequestDto();
        closeRequestDto.setDepositStatus(false);

        when(depositRepository.findByIdAndCustomerId(UUID.fromString(TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID))).thenReturn(Optional.of(testDeposit));

        DepositException exception = assertThrows(DepositException.class, () ->
                depositService.closeDeposit(TEST_DEPOSIT_ID, closeRequestDto, CUSTOMER_ID));

        verify(depositRepository, times(1)).findByIdAndCustomerId(UUID.fromString(TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID));

        assertEquals(BAD_REQUEST, exception.getStatus());
        assertEquals(DEPOSIT_ALREADY_CLOSED, exception.getMessage());
    }

    @Test
    void givenInvalidDepositId_whenCloseDeposit_thenThrowDepositException() {
        CloseDepositRequestDto closeRequestDto = new CloseDepositRequestDto();
        closeRequestDto.setDepositStatus(false);

        when(depositRepository.findByIdAndCustomerId(UUID.fromString(INCORRECT_TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID))).thenReturn(Optional.empty());

        DepositException exception = assertThrows(DepositException.class, () ->
                depositService.closeDeposit(INCORRECT_TEST_DEPOSIT_ID, closeRequestDto, CUSTOMER_ID));

        verify(depositRepository, times(1)).findByIdAndCustomerId(UUID.fromString(INCORRECT_TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID));

        assertEquals(NOT_FOUND, exception.getStatus());
        assertEquals(DATA_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    void givenCloseRequestWithInvalidStatus_whenCloseDeposit_thenThrowDepositException() {
        CloseDepositRequestDto closeRequestDto = new CloseDepositRequestDto();
        closeRequestDto.setDepositStatus(true);

        DepositException exception = assertThrows(DepositException.class, () ->
                depositService.closeDeposit(TEST_DEPOSIT_ID, closeRequestDto, CUSTOMER_ID));

        assertEquals(BAD_REQUEST, exception.getStatus());
        assertEquals(INVALID_DATA, exception.getMessage());
    }

    @Test
    void givenValidDepositIdButInvalidCustomerId_whenCloseDeposit_thenThrowDepositException() {
        CloseDepositRequestDto closeRequestDto = new CloseDepositRequestDto();
        closeRequestDto.setDepositStatus(false);

        when(depositRepository.findByIdAndCustomerId(UUID.fromString(TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID)))
                .thenReturn(Optional.empty());

        DepositException exception = assertThrows(DepositException.class, () ->
                depositService.closeDeposit(TEST_DEPOSIT_ID, closeRequestDto, CUSTOMER_ID));

        verify(depositRepository, times(1)).findByIdAndCustomerId(UUID.fromString(TEST_DEPOSIT_ID), UUID.fromString(CUSTOMER_ID));

        assertEquals(NOT_FOUND, exception.getStatus());
        assertEquals(DATA_NOT_FOUND_MESSAGE, exception.getMessage());
    }
}