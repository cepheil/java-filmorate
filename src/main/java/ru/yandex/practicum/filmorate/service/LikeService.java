package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeRepository;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final EventService eventService;

    public void addLike(Long filmId, Long userId) {
        likeRepository.addLike(filmId, userId);
        eventService.addEvent(userId, filmId, "LIKE", "ADD");
    }

    public void removeLike(Long filmId, Long userId) {
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
