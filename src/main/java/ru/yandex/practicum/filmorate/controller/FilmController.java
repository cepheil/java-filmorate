package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;


    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }


    //GET .../films/{id}
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }


    //GET /films/popular?count={count}
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }


    //PUT /films/{id}/like/{userId}
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId,
                        @PathVariable Long userId) {
        filmService.addLike(filmId, userId);

    }


    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId,
                           @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

}
