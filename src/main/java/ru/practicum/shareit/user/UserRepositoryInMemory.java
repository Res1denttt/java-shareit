package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private static final Set<String> emails = new HashSet<>();
    private static int currentId = 1;

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (emailOccupied(user.getEmail())) {
            throw new ConditionsNotMetException(user.getEmail() + " уже используется");
        }

        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        User existing = users.get(user.getId());
        if (existing == null) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        if (!existing.getEmail().equals(user.getEmail())) {
            if (emailOccupied(user.getEmail())) {
                throw new ConditionsNotMetException("Email " + user.getEmail() + " уже используется");
            }
            emails.remove(existing.getEmail());
            emails.add(user.getEmail());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
        emails.remove(user.getEmail());
    }

    private static long generateId() {
        return currentId++;
    }

    private boolean emailOccupied(String email) {
        return emails.contains(email);
    }
}
