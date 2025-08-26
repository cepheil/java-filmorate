package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final EventService eventService;
    private final ValidationService validationService;

    public void addLike(Long filmId, Long userId) {
        // -8 зафейленных тестов. тест add-like Film id=3 with director add Like ожидает ответ 200,
        // возможно можно заменить на проверку существования лайка, но если он существует мы не должны отправлять 200
        likeRepository.removeLike(filmId, userId);

        likeRepository.addLike(filmId, userId);
        eventService.addEvent(userId, filmId, "LIKE", "ADD");
    }

    public void removeLike(Long filmId, Long userId) {
        validationService.validateFilmExists(filmId); //проверка на удаление лайка несуществующего пользователя
        validationService.validateUserExists(userId);
        likeRepository.removeLike(filmId, userId);
        eventService.addEvent(userId, filmId, "LIKE", "REMOVE");
    }

    public void removeLikesByFilmId(Long filmId) {
        likeRepository.deleteLikesByFilmId(filmId);
    }

    public void removeLikesByUserId(Long userId) {
        likeRepository.deleteLikesByUserId(userId);
    }
}
