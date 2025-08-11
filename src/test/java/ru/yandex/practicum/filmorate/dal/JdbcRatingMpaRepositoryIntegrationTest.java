package ru.yandex.practicum.filmorate.dal;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.RatingMpaRowMapper;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JdbcRatingMpaRepository.class, RatingMpaRowMapper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JdbcRatingMpaRepositoryIntegrationTest {

    @Autowired
    private JdbcRatingMpaRepository ratingMpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM ratings_mpa");
        jdbcTemplate.execute("ALTER TABLE ratings_mpa ALTER COLUMN mpa_id RESTART WITH 1");
    }

    @Test
    void testFindAll() {
        // given
        jdbcTemplate.update("INSERT INTO ratings_mpa (name) VALUES (?)", "G");
        jdbcTemplate.update("INSERT INTO ratings_mpa (name) VALUES (?)", "PG");
        jdbcTemplate.update("INSERT INTO ratings_mpa (name) VALUES (?)", "PG-13");

        // when
        Collection<RatingMpa> ratings = ratingMpaRepository.findAll();

        // then
        assertThat(ratings)
                .hasSize(3)
                .extracting(RatingMpa::getName)
                .containsExactly("G", "PG", "PG-13");
    }

    @Test
    void testFindById_whenExists() {
        // given
        jdbcTemplate.update("INSERT INTO ratings_mpa (name) VALUES (?)", "R");

        // when
        Optional<RatingMpa> ratingOpt = ratingMpaRepository.findById(1L);

        // then
        assertThat(ratingOpt).isPresent();
        assertThat(ratingOpt.get().getName()).isEqualTo("R");
    }

    @Test
    void testFindById_whenNotExists() {
        // when
        Optional<RatingMpa> ratingOpt = ratingMpaRepository.findById(999L);

        // then
        assertThat(ratingOpt).isEmpty();
    }

    @Test
    void testExistsById_true() {
        // given
        jdbcTemplate.update("INSERT INTO ratings_mpa (name) VALUES (?)", "NC-17");

        // when & then
        assertThat(ratingMpaRepository.existsById(1L)).isTrue();
    }

    @Test
    void testExistsById_false() {
        assertThat(ratingMpaRepository.existsById(123L)).isFalse();
    }
}