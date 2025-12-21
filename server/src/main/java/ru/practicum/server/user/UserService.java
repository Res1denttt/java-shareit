package ru.practicum.server.user;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(long userId);

    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);
}
