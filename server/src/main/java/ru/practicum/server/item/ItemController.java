package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.item.CommentDto;
import ru.practicum.dto.item.ItemDto;
import ru.practicum.dto.item.NewCommentDto;
import ru.practicum.dto.item.OwnerItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService service;

    @GetMapping
    public List<OwnerItemDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил GET запрос на все товары пользователя с id = {}", userId);
        return service.findAllForUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Поступил GET запрос на товар с id = {}", itemId);
        return service.findById(userId, itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
        log.info("Поступил POST запрос на добавление товара {} для пользователя с id = {}", itemDto, userId);
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        itemDto.setId(itemId);
        log.info("Поступил PATCH запрос от пользователя с id = {} для товара {}", userId, itemDto);
        return service.update(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Поступил DELETE запрос от пользователя с id = {} на удаление товара с id = {}", userId, itemId);
        service.delete(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Поступил GET запрос на поиск товара, содержаего: {}", text);
        if (text.isBlank()) return List.of();
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @RequestBody NewCommentDto dto) {
        log.info("Поступил Post запрос на создание комменатрия к товару с id = {}", itemId);
        return service.createComment(userId, itemId, dto);
    }
}
