package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(long userId) {
        return repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
    }

    public User save(User user) {
        if (!StringUtils.hasText(user.getEmail())) throw new ConditionsNotMetException("Email должен быть указан");
        return repository.save(user);
    }

    public User update(long userId, UserDto userDto) {
        User oldUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User updatedUser = UserMapper.updateUserFields(oldUser, userDto);
        return repository.update(updatedUser);
    }

    public void delete(long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        repository.delete(user);
    }
}
