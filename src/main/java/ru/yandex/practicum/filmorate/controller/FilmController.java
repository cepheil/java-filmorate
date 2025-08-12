package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }


    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }


    //GET .../films/{id}
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable @Positive Long filmId) {
        return filmService.getFilmById(filmId);
    }


    //DELETE /films/{filmId}
    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable @Positive Long filmId) {
        filmService.deleteFilm(filmId);
    }


    //GET /films/popular?count={count}
    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilms(count);
    }


    //PUT /films/{id}/like/{userId}
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable @Positive Long filmId,
                        @PathVariable @Positive Long userId) {
        filmService.addLike(filmId, userId);
    }


    //DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable @Positive Long filmId,
                           @PathVariable @Positive Long userId) {
        filmService.removeLike(filmId, userId);
    }


}
