package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при попытке обратиться к данным, которые не существуют в системе.
 * Используется для обозначения отсутствия необходимых записей, таких как пользователь или фильм.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
