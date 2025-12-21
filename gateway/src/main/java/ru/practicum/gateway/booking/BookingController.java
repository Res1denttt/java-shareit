package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.booking.BookingDto;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userid, @RequestBody BookingDto dto) {
        return client.create(userid, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return client.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userid, @PathVariable long bookingId) {
        return client.findById(userid, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByState(@RequestHeader("X-Sharer-User-Id") long userid,
                                              @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return client.findByState(userid, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findForOwnerByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return client.findByState(userId, state);
    }
}
