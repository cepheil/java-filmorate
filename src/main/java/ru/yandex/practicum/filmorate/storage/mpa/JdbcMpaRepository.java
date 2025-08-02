package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.base.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMpaRepository extends BaseRepository<MpaRating> implements MpaRepository {

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";

    public JdbcMpaRepository(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<MpaRating> findMpaById(Long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }

    @Override
    public List<MpaRating> findAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }
}
