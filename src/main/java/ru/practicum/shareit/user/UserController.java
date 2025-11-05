package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public List<User> findAll() {
        log.info("Поступил GET запрос на всех пользователей");
        return service.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable long userId) {
        log.info("Поступил GET запрос на пользователя с id = {}", userId);
        return service.findById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил POST запрос на добавление пользователя = {}", user);
        return service.save(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Поступил PATCH запрос на изменение пользователя с id ={}, данные = {}", userId, userDto);
        return service.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Поступил DELETE запрос на удаление пользователя с id = {}", userId);
        service.delete(userId);
    }
}
