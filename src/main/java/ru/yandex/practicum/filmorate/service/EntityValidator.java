package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


@Slf4j
@Service
@RequiredArgsConstructor
public class EntityValidator {
    private final FilmRepository filmRepository;
    private final FriendshipRepository friendshipRepository;
    private final GenreRepository genreRepository;
    private final LikeRepository likeRepository;
    private final RatingMpaRepository ratingMpaRepository;
    private final UserRepository userRepository;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);


    // Проверка пользователя
    public void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("Пользователь не может быть null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

    }

    // Проверка существования пользователя
    public void validateUserExists(Long userId) {
        if (userId == null) {
            log.error("ID пользователя не может быть null");
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (!userRepository.existsById(userId)) {
            log.error("Пользователь с Id =  {} не найден", userId);
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }
    }

    // Проверка уникальности почты
    public void validateUserEmailUniqueness(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Email не может быть пустым");
            throw new ValidationException("Email не может быть пустым");
        }
        boolean exists = userRepository.existsByEmail(user.getEmail().trim());
        if (exists) {
            log.error("Этот Email уже используется: {}", user.getEmail());
            throw new DuplicatedDataException("Этот Email уже используется: " + user.getEmail());
        }
    }


    public void validateUserForUpdate(User user) {
        if (user == null || user.getId() == null) {
            log.error("user и userID не могут быть пустыми");
            throw new ValidationException("user и userID не могут быть пустыми");
        }
        validateUserExists(user.getId());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            boolean exists = userRepository.isDuplicateEmail(user.getId(), user.getEmail());
            if (exists) {
                log.error("Этот Email уже используется: {}", user.getEmail());
                throw new DuplicatedDataException("Этот Email уже используется: " + user.getEmail());
            }
        }
        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            boolean loginExists = userRepository.isDuplicateLogin(user.getId(), user.getLogin());
            if (loginExists) {
                log.error("Этот логин уже используется: {}", user.getLogin());
                throw new DuplicatedDataException("Этот логин уже используется: " + user.getLogin());
            }
        }

    }


    // Проверка фильма
    public void validateFilm(Film film) {
        if (film == null) {
            log.error("Фильм не может быть null");
            throw new ValidationException("Фильм не может быть null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        // Дополнительная проверка рейтинга
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            validateRatingExists(film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                validateGenreExists(genre.getId());

            }

        }

    }

//    public void validateFilmUniqueness(Film film) {
//        // Проверяем, существует ли фильм с таким же названием и датой релиза
//        boolean exists = filmRepository.existsByNameAndReleaseDate(
//                film.getName(),
//                film.getReleaseDate()
//        );
//        if (exists) {
//            log.error("Фильм {} c датой релиза {} уже существует", film.getName(), film.getReleaseDate());
//            throw new DuplicatedDataException(
//                    "Фильм '" + film.getName() + "' (" + film.getReleaseDate() + ") уже существует"
//            );
//        }
//    }


    // Проверка существования фильма
    public void validateFilmExists(Long filmId) {
        if (filmId == null) {
            log.error("ID фильма не может быть null");
            throw new ValidationException("ID фильма не может быть null");
        }
        if (!filmRepository.existsById(filmId)) {
            log.error("Фильм с Id =  {} не найден", filmId);
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден");
        }
    }

    // Проверка существования рейтинга MPA
    public void validateRatingExists(Long ratingId) {
        if (ratingId == null) {
            log.error("ID рейтинга не может быть null");
            throw new ValidationException("ID рейтинга не может быть null");
        }
        if (!ratingMpaRepository.existsById(ratingId)) {
            throw new NotFoundException("Рейтинг MPA с ID=" + ratingId + " не найден");
        }
    }

    // Проверка существования жанра
    public void validateGenreExists(Long genreId) {
        if (genreId == null) {
            log.error("ID жанра не может быть null");
            throw new ValidationException("ID жанра не может быть null");
        }
        if (!genreRepository.existsById(genreId)) {
            log.error("Жанр: {}  не найден", genreId);
            throw new NotFoundException("Жанр с ID=" + genreId + " не найден");
        }
    }

    // Проверка операции с друзьями
    public void validateFriendshipOperation(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        if (userId == friendId) {
            log.error("Пользователь не может добавить самого себя в друзья");
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }
    }


    // Проверка операции с лайками
    public void validateLikeOperation(Long filmId, Long userId) {
        validateFilmExists(filmId);
        validateUserExists(userId);
    }

}


