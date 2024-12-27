package org.aston.depositservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.aston.depositservice.service.DepositService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.aston.depositservice.configuration.ApplicationConstant.ACCOUNT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.ACTIVE_DEPOSIT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.DATA_NOT_FOUND_MESSAGE;
import static org.aston.depositservice.configuration.ApplicationConstant.DEPOSIT_ALREADY_CLOSED;
import static org.aston.depositservice.configuration.ApplicationConstant.DEPOSIT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_DATA;
import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_ACCESS;
import static org.aston.depositservice.configuration.ApplicationConstant.PRODUCT_NOT_FOUND;
import static org.aston.depositservice.configuration.ApplicationConstant.STATUS_IS_ALREADY_SET;
import static org.aston.depositservice.configuration.ApplicationConstant.UPDATE_AUTORENEWAL_STATUS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@AllArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositMapper depositMapper;
    private final DepositAccountRepository depositAccountRepository;
    private final ProductRepository productRepository;
    private final DepositRepository depositRepository;
    private static final String AUTHENTICATED_CUSTOMER_STATUS = "1";

    @Override
    public List<ActiveDepositListResponseDto> getActiveDepositList(String customerId) {
        List<ActiveDepositListResponseDto> activeDepositListResponseDtoList = depositRepository.findALLActiveDepositByCustomerId(UUID.fromString(customerId), true);
        if (activeDepositListResponseDtoList.isEmpty()) {
            throw new DepositException(ACTIVE_DEPOSIT_NOT_FOUND);
        }
        log.info("Успешно получены действующие депозиты для клиента: {}", customerId);
        return activeDepositListResponseDtoList;
    }

    @Override
    public AutorenewalStatusResponseDto changeAutorenewalStatus(String depositId,
                                                                AutorenewalRequestDto requestDto,
                                                                String customerId) {
        Deposit depositFromDb = depositRepository.findByIdAndCustomerIdAndDepositStatus(UUID.fromString(depositId), UUID.fromString(customerId), true)
                .orElseThrow(() -> new DepositException(HttpStatus.NOT_FOUND, DATA_NOT_FOUND_MESSAGE));
        if (depositFromDb.getAutorenStatus().equals(requestDto.getAutorenewal())) {
            throw new DepositException(HttpStatus.BAD_REQUEST, STATUS_IS_ALREADY_SET);
        }
        depositFromDb.setAutorenStatus(requestDto.getAutorenewal());
        depositRepository.save(depositFromDb);
        return new AutorenewalStatusResponseDto(UPDATE_AUTORENEWAL_STATUS, depositFromDb.getAutorenStatus());
    }

    @Override
    public ActiveDepositResponseDto getActiveCustomerDeposit(String customerId, String depositId) {
        Optional<ActiveDepositResponseDto> activeDepositResponseDto = Optional.ofNullable(depositRepository
                .findActiveDepositByDepositId(UUID.fromString(depositId), UUID.fromString(customerId)));
        if (activeDepositResponseDto.isEmpty()) {
            throw new DepositException(NOT_FOUND, DATA_NOT_FOUND_MESSAGE);
        }
        log.info("Подробная информация по депозиту {} с id клиента {} получена: ", depositId, customerId);
        return activeDepositResponseDto.get();
    }

    @Override
    @Transactional
    public CloseDepositResponseDto closeDeposit(String depositId,
                                                CloseDepositRequestDto closeDepositRequestDto,
                                                String customerId) {
        if (closeDepositRequestDto.getDepositStatus().equals(true)) {
            log.error("Входной параметр depositStatus должен быть false");
            throw new DepositException(BAD_REQUEST, INVALID_DATA);
        }
        Deposit depositFromDb = depositRepository.findByIdAndCustomerId(UUID.fromString(depositId), UUID.fromString(customerId))
                .orElseThrow(() -> new DepositException(HttpStatus.NOT_FOUND, DATA_NOT_FOUND_MESSAGE));
        if (depositFromDb.getDepositStatus().equals(closeDepositRequestDto.getDepositStatus())) {
            throw new DepositException(BAD_REQUEST, DEPOSIT_ALREADY_CLOSED);
        }
        depositFromDb.setDepositStatus(closeDepositRequestDto.getDepositStatus());
        depositFromDb.setCloseDate(Instant.now());
        depositRepository.save(depositFromDb);
        log.info("Статус депозита {} изменён на \"закрыт\"", depositId);
        return depositMapper.depositToCloseDepositResponseDto(depositFromDb);
    }

    @Override
    public NewDepositResponseDto createDeposit(NewDepositRequestDto dto, String customerStatus, String customerId) {
        Product product = productRepository.findById(UUID.fromString(dto.getProductId()))
                .orElseThrow(() -> new DepositException(PRODUCT_NOT_FOUND));
        DepositAccount depositAccount = depositAccountRepository.findById(UUID.fromString(dto.getAccountId()))
                .orElseThrow(() -> new DepositException(ACCOUNT_NOT_FOUND));
        if (!customerStatus.equals(AUTHENTICATED_CUSTOMER_STATUS)) {
            throw new DepositException(FORBIDDEN, MISSING_ACCESS);
        }
        Deposit deposit = depositMapper.dtoToDeposit(dto, product, depositAccount,
                true, true, customerId, product.getAmountMin());
        Deposit savedDeposit = depositRepository.save(deposit);

        log.info("Данные депозита uuid={} успешно сохранены", savedDeposit.getId());

        return depositMapper.depositToDto(savedDeposit, product.getName());
    }

    @Override
    public void autoCloseDeposit() {
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
        Map<UUID, Deposit> depositMap = depositRepository.getReadyToCloseDeposit(startOfDay, endOfDay);
        depositMap.forEach((k, v) -> {
            v.setDepositStatus(false);
            log.info("Автоматическое закрытие депозита с uuid {}", k);
        });
        depositRepository.saveAll(depositMap.values());
    }

    @Override
    @Transactional
    public ProlongDepositResponseDto prolongDeposit(ProlongDepositRequestDto request) {
        UUID depositId = UUID.fromString(request.getDepositId());

        log.info("Поиск депозита с id {}", depositId);
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new DepositException(DEPOSIT_NOT_FOUND));

        LocalDateTime currentClosedAt = deposit.getEndDate().atZone(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime newClosedAt = currentClosedAt.plusDays(request.getRenewalTermsDays());

        deposit.setEndDate(newClosedAt.toInstant(ZoneOffset.UTC));

        Deposit updatedDeposit = depositRepository.save(deposit);
        log.info("Депозит {} успешно пролонгирован", updatedDeposit.getId());

        return depositMapper.depositToProlongDepositResponseDto(updatedDeposit);
    }
}
