package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;
import java.util.Set;

public interface LikeRepository {

    int addLike(Long filmId, Long userId);

    int removeLike(Long filmId, Long userId);

    int removeAllLikes(Long filmId);

    void addLikesBatch(Long filmId, Set<Long> userIds);

    void loadLikesForFilms(Map<Long, Film> filmMap);
}
