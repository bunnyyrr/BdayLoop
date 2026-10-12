package ru.bdayloop.repository;

import ru.bdayloop.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Integer id);
    Optional<User> findByUsername(String username);
    List<User> search(String text);
    void delete(User user);
}
