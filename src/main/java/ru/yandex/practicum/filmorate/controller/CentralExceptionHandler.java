package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.stream.Collectors;

/**
 * Класс {@code CentralExceptionHandler} представляет собой глобальный обработчик исключений для всего приложения.
 * Используется для централизованного перехвата и обработки исключений, возникающих в контроллерах.
 *
 * <p>Обрабатывает следующие типы исключений:</p>
 * <ul>
 *     <li>{@link MethodArgumentNotValidException} — ошибки валидации DTO-объектов</li>
 *     <li>{@link ValidationException} — пользовательские ошибки валидации</li>
 *     <li>{@link NotFoundException} — объект не найден</li>
 *     <li>{@link DuplicatedDataException} — попытка добавления дублирующихся данных</li>
 *     <li>{@link Exception} — любые прочие непредвиденные ошибки</li>
 * </ul>
 *
 * <p>Все исключения преобразуются в понятные JSON-ответы с соответствующим HTTP-статусом.</p>
 */
@Slf4j
@RestControllerAdvice
public class CentralExceptionHandler {

    /**
     * Обрабатывает исключения типа {@link MethodArgumentNotValidException}.
     * Возникает, когда переданные клиентом данные не прошли валидацию (например, неверный формат email).
     *
     * @param e исключение, содержащее информацию о результатах валидации
     * @return объект {@link ErrorResponse} с детализированной информацией об ошибках
     */
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        log.warn("Ошибка валидации: {}", errorMessage);
        return new ErrorResponse(errorMessage);
    }

    /**
     * Обрабатывает исключения типа {@link ValidationException}.
     * Используется для пользовательских проверок бизнес-логики (например, пустое имя).
     *
     * @param e исключение с текстом ошибки
     * @return объект {@link ErrorResponse} с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Ошибка валидации: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link NotFoundException}.
     * Вызывается, когда запрашиваемый объект (пользователь, фильм и т.д.) не найден.
     *
     * @param e исключение с текстом ошибки
     * @return объект {@link ErrorResponse} с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Объект не найден: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link DuplicatedDataException}.
     * Возникает при попытке добавить данные, которые уже существуют (например, дублирующий email).
     *
     * @param e исключение с текстом ошибки
     * @return объект {@link ErrorResponse} с сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateDataException(final DuplicatedDataException e) {
        log.warn("Конфликт данных: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает все остальные непойманные исключения.
     * Используется как последнее средство для предотвращения падения сервера без ответа.
     *
     * @param e любое необработанное исключение
     * @return объект {@link ErrorResponse} с общим сообщением об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(final Exception e) {
        log.warn("Ошибка сервера: {}", e.getMessage(), e);
        return new ErrorResponse("Внутренняя ошибка сервера.");
    }
}
