package ru.practicum.server.booking.strategy;

import org.springframework.data.domain.Sort;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingState;

import java.util.List;

public interface BookingFetchStateStrategy {

    List<Booking> getBookings(long userId, Sort sort);

    BookingState getStrategyState();
}
