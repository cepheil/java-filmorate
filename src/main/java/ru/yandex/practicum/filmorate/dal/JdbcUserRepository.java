package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("jdbc")
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)"; // returning id"
    private static final String UPDATE_QUERY =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM users WHERE id = ?";

    public JdbcUserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        Long id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public boolean delete(Long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_USER_ID_QUERY, id);
    }



    @Override
    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean isDuplicateEmail(Long userId, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE id <> ? AND LOWER(TRIM(email)) = LOWER(TRIM(?))";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, email);
        return count != null && count > 0;
    }

    @Override
    public boolean isDuplicateLogin(Long userId, String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE id <> ? AND LOWER(TRIM(login)) = LOWER(TRIM(?))";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, login);
        return count != null && count > 0;
    }


    @Override
    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(TRIM(login)) = LOWER(TRIM(?))";
        Integer count = jdbc.queryForObject(sql, Integer.class, login);
        return count != null && count > 0;
    }


}
