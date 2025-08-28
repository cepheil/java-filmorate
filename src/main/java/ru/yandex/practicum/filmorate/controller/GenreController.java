package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {
    private final GenreService genreService;

    /**
     * Получить список всех жанров.
     *
     * @return коллекция всех жанров
     */
    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Попытка получения всех жанров");
        return genreService.findAllGenres();
    }

    /**
     * Получить жанр по ID.
     *
     * @param id идентификатор жанра
     * @return жанр с указанным ID
     * NotFoundException если жанр не найден
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        log.info("Попытка получения жанра по ID: {}", id);
        return genreService.findGenreById(id);
    }
}
