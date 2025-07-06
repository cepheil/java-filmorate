package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс {@code ErrorResponse} представляет стандартный формат ответа приложения в случае ошибки.
 * Используется в глобальном обработчике исключений
 * {@link ru.yandex.practicum.filmorate.controller.CentralExceptionHandler}
 * для возврата клиенту понятного JSON-ответа при возникновении ошибок.</p>
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
}
