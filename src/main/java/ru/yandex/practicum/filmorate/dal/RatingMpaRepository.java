package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.Optional;

public interface RatingMpaRepository {

    Collection<RatingMpa> findAll();

    Optional<RatingMpa> findById(Long id);

    boolean existsById(long id);

}
