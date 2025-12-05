package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.NewRequestDto;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient client;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody NewRequestDto request) {
        return client.create(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> findUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return client.findUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return client.findAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable("requestId") long requestId) {
        return client.findById(requestId);
    }
}
