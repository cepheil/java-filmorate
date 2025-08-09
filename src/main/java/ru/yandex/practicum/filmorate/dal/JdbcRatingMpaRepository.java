package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.RatingMpaRowMapper;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.Optional;


@Repository
@Qualifier("jdbc")
public class JdbcRatingMpaRepository extends BaseRepository<RatingMpa> implements RatingMpaRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM ratings_mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings_mpa WHERE mpa_id = ?";


    public JdbcRatingMpaRepository(JdbcTemplate jdbc, RatingMpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<RatingMpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<RatingMpa> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM ratings_mpa WHERE mpa_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
