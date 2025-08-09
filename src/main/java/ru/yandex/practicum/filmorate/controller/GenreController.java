package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;


    //    GET /genres — возвращает список объектов содержащих жанр
    @GetMapping
    public Collection<Genre> findAllGenres(){
        return genreService.findAllGenres();
    }


    //    GET /genres/{id} возвращает объект содержащий жанр с идентификатором id
    @GetMapping("/{id}")
    public Genre findGenreById (@PathVariable @Positive Long id) {
        return genreService.findGenreById(id);
    }

}
