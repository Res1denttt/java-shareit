package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> findAll() {
        return repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto findById(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto create(UserDto dto) {
        if (!StringUtils.hasText(dto.getEmail())) throw new ConditionsNotMetException("Email должен быть указан");
        User user = UserMapper.mapToUser(dto);
        User created = repository.save(user);
        return UserMapper.mapToUserDto(created);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        User oldUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User updatedUser = UserMapper.updateUserFields(oldUser, userDto);
        User dbUser = repository.save(updatedUser);
        return UserMapper.mapToUserDto(dbUser);
    }

    @Override
    public void delete(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        repository.delete(user);
    }
}
