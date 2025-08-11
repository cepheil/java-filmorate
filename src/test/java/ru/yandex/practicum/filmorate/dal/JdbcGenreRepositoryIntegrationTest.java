package ru.yandex.practicum.filmorate.dal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import ru.yandex.practicum.filmorate.dal.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@JdbcTest
@Import({JdbcGenreRepository.class, GenreRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JdbcGenreRepositoryIntegrationTest {

    @Autowired
    private JdbcGenreRepository genreRepository;

    @Test
    public void testFindGenreById() {
        Long genreId = 1L; // 'Комедия' в data.sql

        Optional<Genre> genreOptional = genreRepository.findById(genreId);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre.getId()).isEqualTo(genreId);
                    assertThat(genre.getName()).isEqualTo("Комедия");
                });
    }

    @Test
    public void testFindAllGenres() {
        Collection<Genre> genres = genreRepository.findAll();

        assertThat(genres).isNotEmpty();
        assertThat(genres).extracting(Genre::getName)
                .contains("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }


}