package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService service;

    @PostMapping
    public ResponseBookingDto create(@RequestHeader("X-Sharer-User-Id") long userid, @RequestBody BookingDto dto) {
        log.info("Поступил POST запрос от пользователя с id = {} на создание запроса на бронирование: {}", userid, dto);
        return service.create(userid, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        log.info("Поступил PATCH запрос на изменение статуса бронирование с id = {} на {} от пользователя с id = {}",
                bookingId, approved, userId);
        return service.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findById(@RequestHeader("X-Sharer-User-Id") long userid, @PathVariable long bookingId) {
        log.info("Поступил GET запрос от пользователя с id = {} на получение бронирования с id = {}", userid, bookingId);
        return service.findById(userid, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> findByState(@RequestHeader("X-Sharer-User-Id") long userid,
                                                @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("Поступил GET запрос от пользователя с id = {} на получение списка бронирований со статусом = {}",
                userid, state);
        return service.findByState(userid, state);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> findForOwnerByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("Поступил GET запрос от пользователя с id = {} на получение списка бронирований своих товаров со статусом = {}",
                userId, state);
        return service.findForOwnerByState(userId, state);
    }
}
