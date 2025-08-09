package ru.yandex.practicum.filmorate.dal;

public interface LikeRepository {

    int addLike(Long filmId, Long userId);

    int removeLike(Long filmId, Long userId);

    int removeAllLikes(Long filmId);
}
