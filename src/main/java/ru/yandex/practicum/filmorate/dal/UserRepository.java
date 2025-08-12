package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    boolean delete(Long id);

    Optional<User> findById(Long id);

    boolean existsById(long id);

    boolean existsByEmail(String email);

    boolean isDuplicateEmail(Long userId, String email);

    boolean existsByLogin(String login);

    public boolean isDuplicateLogin(Long userId, String login);
}
