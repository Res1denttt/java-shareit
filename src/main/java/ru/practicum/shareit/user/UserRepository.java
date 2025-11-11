package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(long id);

    User save(User user);

    User update(User user);

    void delete(User user);
}
