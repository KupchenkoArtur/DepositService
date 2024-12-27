package org.aston.depositservice.configuration;

public class ApplicationConstant {

    public static final String HEADER_KEY_CUSTOMER_ID = "Customer-Id";

    public static final String HEADER_KEY_CUSTOMER_STATUS = "Customer-Status";

    public static final String DATA_NOT_FOUND_MESSAGE = "Данные не найдены";

    public static final String DEPOSIT_ID = "deposit_id";

    public static final String MISSING_PARAMETER = "Отсутствует параметр";

    public static final String UNSUPPORTED_DATA_TYPE = "Неподдерживаемый тип данных";

    public static final String PRODUCT_NOT_FOUND = "Информация по депозитному продукту не найдена";

    public static final String PAGE_NOT_FOUND = "Страница не найдена";

    public static final String ACCOUNT_NOT_FOUND = "Аккаунт не найден";

    public static final String STATUS_IS_ALREADY_SET = "Данные уже внесены";

    public static final String DEPOSIT_NOT_FOUND = "Депозит не найден";

    public static final String UPDATE_AUTORENEWAL_STATUS = "Статус автопродления успешно обновлен.";

    public static final String RENEWAL_TERMS_DAYS_MUST_BE_POSITIVE_NUMBER = "renewalTermsDays должно быть положительным";

    public static final String ACTIVE_DEPOSIT_NOT_FOUND = "Действующие депозиты не найдены";

    public static final String MISSING_ACCESS = "Отсутствуют права доступа";

    public static final String REGEXP_UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String REGEXP_PERCENT_RATE = "^\\d{1,2}(\\.\\d{1,2})?$";

    public static final String INVALID_UUID = "Некорректный формат UUID";

    public static final String INVALID_DATA = "Некорректный запрос";

    public static final String MISSING_REQUEST_HEADERS = "Отсутствуют заголовки в запросе";

    public static final String DEPOSIT_ALREADY_CLOSED = "Депозит уже закрыт";

    public static final String EXCEEDED_NUMBER_ATTEMPTS = "Превышено максимальное количество попыток доступа к базе данных.";

    public static final String REGEX_TIME_DAYS = "^\\d+$";
}
