package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.review.ReviewRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final FilmRepository filmRepository;
    private final FriendService friendService;
    private final LikeService likeService;
    private final GenreRepository genreRepository;
    private final ReviewRepository reviewRepository;

    public Collection<User> findAllUsers() {
        log.info("Попытка получения списка всех пользователей.");
        return userRepository.findAllUsers();
    }

    public User getUserById(Long userId) {
        log.info("Попытка получения пользователя по ID: {}", userId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null.");
        }
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
    }

    public User createUser(User user) {
        log.info("Попытка создания нового пользователя: email={}, login={}", user.getEmail(), user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User createdUser = userRepository.createUser(user);
        log.info("Создан пользователь с ID: {}", createdUser.getId());
        return createdUser;
    }

    public User updateUser(User newUser) {
        log.info("Попытка обновления пользователя с ID: {}", newUser.getId());
        if (newUser.getId() == null) {
            throw new ValidationException("ID пользователя не может быть null.");
        }
        validationService.validateUserExists(newUser.getId());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        User updatedUser = userRepository.updateUser(newUser);
        log.info("Пользователь с ID {} обновлен", newUser.getId());
        return updatedUser;
    }

    public Collection<Film> getRecommendedFilms(Long userId) {
        Collection<Film> filmList = filmRepository.getRecommendedFilms(userId);
        for (Film film : filmList) {
            Set<Genre> genres = genreRepository.findGenreByFilmId(film.getId());
            film.setGenres(genres);
            List<Review> reviews = reviewRepository.getReviewsByFilmId(film.getId(), Integer.MAX_VALUE);
            film.setReviews(reviews);
        }
        log.info("Отгрузил {} рекомендованных фильмов для пользователя {}", filmList.size(),
                userId);
        return filmList;
    }

    public void removeUser(Long userId) {
        log.info("Попытка удаления пользователя {} ", userId);
        validationService.validateUserExists(userId);
        userRepository.deleteUser(userId);
        friendService.removeAllFriendsByUserId(userId);
        //так как не оговорено особо, решил, что при удалении пользователя его лайки нужно удалять
        likeService.removeLikesByUserId(userId);
        log.info("Пользователь {}, а также связанные с ним записи о лайках и друзьях удалены", userId);
    }
}
