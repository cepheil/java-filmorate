package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.base.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userRepository")
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_USERS_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, " +
            "birthday = ? WHERE user_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ?";

    public JdbcUserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAllUsers() {
        return findMany(FIND_ALL_USERS_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return findOne(FIND_USER_BY_ID_QUERY, userId);
    }

    @Override
    public User createUser(User user) {
        long id = insert(
                INSERT_USERS_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public boolean deleteUser(Long id) {
        return delete(DELETE_USER_QUERY, id);
    }
}
