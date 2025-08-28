package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {
    private final MpaRatingService mpaRatingService;

    /**
     * Получить список всех рейтингов MPA.
     *
     * @return коллекция всех рейтингов MPA
     */
    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        log.info("Попытка получения всех рейтингов MPA");
        return mpaRatingService.findAllMpa();
    }

    /**
     * Получить рейтинг MPA по ID.
     *
     * @param id идентификатор рейтинга MPA
     * @return рейтинг MPA с указанным ID
     * NotFoundException если рейтинг не найден
     */
    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable Long id) {
        log.info("Попытка получения рейтинга MPA по ID: {}", id);
        return mpaRatingService.findMpaById(id);
    }
}
