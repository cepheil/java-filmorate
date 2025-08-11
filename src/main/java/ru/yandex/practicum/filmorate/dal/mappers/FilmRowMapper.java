package ru.yandex.practicum.filmorate.dal.mappers;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));


        Long ratingId = rs.getLong("mpa_id");
        if (!rs.wasNull()) {
            String ratingName = rs.getString("mpa_name");
            film.setMpa(new RatingMpa(ratingId, ratingName));
        }

        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        return film;
    }
}
