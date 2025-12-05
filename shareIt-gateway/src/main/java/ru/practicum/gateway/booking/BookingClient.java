package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.booking.BookingDto;
import ru.practicum.gateway.configuration.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String URL = "/bookings";

    @Value("${server.host}")
    private String host;

    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(long id, BookingDto dto) {
        return post(host + URL, id, dto);
    }

    public ResponseEntity<Object> approve(long userId, long bookingId, boolean approved) {
        return patch(host + URL + "/" + bookingId + "?approved={approved}",
                userId,
                Map.of("approved", approved),
                null);
    }

    public ResponseEntity<Object> findById(long userId, long bookingId) {
        return get(host + URL + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> findByState(long userId, BookingState state) {
        return get(host + URL + "?state={state}", userId, Map.of("state", state));
    }

}
