package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository jdbcGenreRepository;

    public Collection<Genre> findAllGenres() {
        log.info("GET /genres - получение списка всех жанров");
        return jdbcGenreRepository.findAll();
    }

    public Genre findGenreById(Long id) {
        log.info("GET /genres/{id} - получение жанра с идентификатором id");
        return jdbcGenreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID=" + id + " не найден"));
    }

}
