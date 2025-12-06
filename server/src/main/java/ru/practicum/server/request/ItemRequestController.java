package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ItemRequestDto;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.dto.request.ShortItemRequest;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody NewRequestDto request) {
        log.info("Поступил POST запрос от пользователя с id = {} на создание запроса товара = {}", userId, request);
        return itemRequestService.create(userId, request);
    }

    @GetMapping
    public List<ItemRequestDto> findUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил GET запрос от пользователя с id = {} на получение списка своих запросов товаров", userId);
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ShortItemRequest> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил GET запрос от пользователя с id = {} на получение списка всех запросов товаров", userId);
        return itemRequestService.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable("requestId") long requestId) {
        log.info("Поступил GET запрос на получение запроса товара с id = {}", requestId);
        return itemRequestService.findById(requestId);
    }
}
