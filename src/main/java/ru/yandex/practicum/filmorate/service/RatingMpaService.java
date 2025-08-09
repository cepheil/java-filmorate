package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingMpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class RatingMpaService {
    private final EntityValidator entityValidator;
    private final RatingMpaRepository jdbcRatingMpaRepository;

    public Collection<RatingMpa> findAllRatingMpa() {
        log.info("GET /mpa - получение списка всех рейтингов");
        return jdbcRatingMpaRepository.findAll();
    }

    public RatingMpa findRatingMpaById(Long id) {
        log.info("GET /mpa/{id} - получение рейтинга с идентификатором id");
        entityValidator.validateRatingExists(id);
        return jdbcRatingMpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID=" + id + " не найден"));
    }

}
