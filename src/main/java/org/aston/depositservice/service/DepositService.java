package org.aston.depositservice.service;

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

import java.util.List;

/**
 * Сервис для работы с депозитами.
 * Этот интерфейс предоставляет методы для поиска депозитов
 */
public interface DepositService {

    /**
     * Получить список действующих депозитов.
     *
     * @param customerId Идентификатор пользователя, по которому производится поиск.
     * @return Список действующих депозитов.
     */
    List<ActiveDepositListResponseDto> getActiveDepositList(String customerId);

    /**
     * Изменяем дату закрытия депозита CloseAt с учетом RenewalTermsDays
     *
     * @param request Данные для пролонгации депозита
     * @return Дто с данными о депозите и обновленной` дате закрытия
     */
    ProlongDepositResponseDto prolongDeposit(ProlongDepositRequestDto request);

    /**
     * Получить информацию по действующему депозиту.
     *
     * @param depositId идентификатор депозита
     * @return Подробная информация о депозите
     */
    ActiveDepositResponseDto getActiveCustomerDeposit(String customerId, String depositId);

    /**
     * Включение и отключение автопродления депозита.
     *
     * @param depositId             Идентификатор депозита, по которому производится поиск.
     * @param autorenewalRequestDto Dto со статусом автопродления.
     * @param customerId            Идентификатор пользователя
     * @return Установленный статус автопродления.
     */
    AutorenewalStatusResponseDto changeAutorenewalStatus(String depositId,
                                                         AutorenewalRequestDto autorenewalRequestDto,
                                                         String customerId);

    /**
     * Закрытие выбранного депозита.
     *
     * @param depositId              идентификатор депозита, который будет закрыт.
     * @param closeDepositRequestDto Dto с данными для закрытия депозита.
     * @param customerId             Идентификатор пользователя.
     * @return CloseDepositResponseDto c данными о закрытом депозите
     */
    CloseDepositResponseDto closeDeposit(String depositId, CloseDepositRequestDto closeDepositRequestDto, String customerId);

    /**
     * Создать новый депозит.
     *
     * @param newDepositRequestDto Данные для создания депозита
     * @param customerStatus       Статус клиента
     * @param customerId           Идентификатор клиента
     * @return Информация о созданном депозите
     */
    NewDepositResponseDto createDeposit(NewDepositRequestDto newDepositRequestDto, String customerStatus, String customerId);

    /**
     * Автоматическое закрытие депозита после начиления процентов.
     */
    void autoCloseDeposit();
}
