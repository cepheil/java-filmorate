package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации данных.
 * Используется для обозначения некорректного формата или значений входных данных.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
