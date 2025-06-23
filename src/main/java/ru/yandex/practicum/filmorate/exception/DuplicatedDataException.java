package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при попытке добавить данные, которые уже существуют в системе.
 * Используется для обозначения дублирования уникальных записей, таких как фильмы или пользователи.
 */
public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(String message) {
        super(message);
    }
}
