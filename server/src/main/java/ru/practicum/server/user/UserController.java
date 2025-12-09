package ru.practicum.server.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Поступил GET запрос на всех пользователей");
        return service.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) {
        log.info("Поступил GET запрос на пользователя с id = {}", userId);
        return service.findById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Поступил POST запрос на добавление пользователя = {}", userDto);
        return service.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Поступил PATCH запрос на изменение пользователя с id ={}, данные = {}", userId, userDto);
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Поступил DELETE запрос на удаление пользователя с id = {}", userId);
        service.delete(userId);
    }
}
