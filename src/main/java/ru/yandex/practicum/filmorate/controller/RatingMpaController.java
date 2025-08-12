package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingMpaController {
    private final RatingMpaService ratingMpaService;


    //    GET /mpa — возвращает список объектов содержащих рейтинг
    @GetMapping
    public Collection<RatingMpa> findAllRatingMpa() {
        return ratingMpaService.findAllRatingMpa();
    }


    //    GET /mpa/{id} — возвращает объект содержащий рейтинг с идентификатором id
    @GetMapping("/{id}")
    public RatingMpa findRatingMpaById(@PathVariable @Positive Long id) {
        return ratingMpaService.findRatingMpaById(id);
    }

}
